package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cross_domain_validation.ValidationResult;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.Router;
import com.mt.access.domain.model.endpoint.RouterId;
import com.mt.access.domain.model.endpoint.RouterQuery;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleType;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CrossDomainValidationService {

    @Value("${mt.misc.mgmt-email}")
    private String adminEmail;

    public void validate(TransactionContext context) {
        log.debug("start of validate existing data");
        Optional<ValidationResult> validationResult1 =
            DomainRegistry.getValidationResultRepository().query();
        ValidationResult validationResult;
        validationResult = validationResult1.orElseGet(ValidationResult::create);
        if (validationResult.failedTooManyTimes()) {
            log.warn("end of validate job, job is paused due to continuous failure");
            return;
        }
        boolean b = validateCacheProfileAndEndpoint(context);
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        boolean b5 = false;
        boolean b6 = false;
        boolean b7 = false;
        boolean b8 = false;
        boolean b9 = false;
        if (b) {
            log.debug("check client and endpoint");
            b1 = validateRouterAndEndpoint(context);
            if (b1) {
                log.debug("check client and project");
                b2 = validateClientAndProject(context);
                if (b2) {
                    log.debug("check client and role");
                    b3 = validateClientAndRole(context);
                    if (b3) {
                        log.debug("check cors and endpoint");
                        b4 = validateCorsProfileAndEndpoint(context);
                        if (b4) {
                            log.debug("check project and role");
                            b5 = validateProjectAndRole(context);
                            if (b5) {
                                log.debug("check project and user");
                                b6 = validateProjectAndUser(context);
                                if (b6) {
                                    log.debug("check endpoint and permission");
                                    b7 = validateEndpointAndPermission(context);
                                    if (b7) {
                                        log.debug("check role and permission");
                                        b8 = validateRoleAndPermission(context);
                                        if (b8) {
                                            log.debug("check user and user relation");
                                            b9 = validateUserAndUserRelation(context);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        ValidationResult next;
        if (b && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9) {
            next = validationResult.reset();
        } else {
            next = validationResult.increaseFailureCount(context, adminEmail);
        }
        if (validationResult1.isEmpty()) {
            DomainRegistry.getValidationResultRepository().create(next);
        } else {
            DomainRegistry.getValidationResultRepository().update(next);
        }
        log.debug("end of validate existing data");
    }

    /**
     * user id present in user relation must present in user table
     *
     * @return if all user id can be found
     */
    private boolean validateUserAndUserRelation(TransactionContext context) {
        Set<UserId> userIds = DomainRegistry.getUserRelationRepository().getUserIds();
        Set<UserId> userIds2 = DomainRegistry.getUserRepository().getIds();
        boolean b = userIds.size() == userIds2.size();
        if (!b) {
            Set<UserId> missing =
                userIds.stream().filter(userId -> !userIds2.contains(userId)).limit(5)
                    .collect(Collectors.toSet());
            ValidationFailed event =
                new ValidationFailed("UNABLE_TO_FIND_ALL_USER_FOR_USER_RELATION");
            event.addMessage(convertDomainIds(missing));
            context.append(event);
        }
        return b;
    }

    /**
     * make sure role's permission id exit.
     */
    private boolean validateRoleAndPermission(TransactionContext context) {
        Set<Role> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e), RoleQuery.all());
        Set<PermissionId> collect =
            allByQuery.stream().flatMap(e -> {
                    Set<PermissionId> totalPerm = new HashSet<>();
                    Set<PermissionId> comPerm =
                        DomainRegistry.getCommonPermissionIdRepository().query(e);
                    Set<PermissionId> apiPerm = DomainRegistry.getApiPermissionIdRepository().query(e);
                    Set<PermissionId> extPerm =
                        DomainRegistry.getExternalPermissionIdRepository().query(e);
                    totalPerm.addAll(comPerm);
                    totalPerm.addAll(apiPerm);
                    totalPerm.addAll(extPerm);
                    return totalPerm.stream();
                })
                .collect(Collectors.toSet());
        Set<PermissionId> permissionIds =
            DomainRegistry.getPermissionRepository().allPermissionId();
        if (collect.stream().anyMatch(e -> !permissionIds.contains(e))) {
            Set<PermissionId> missing =
                collect.stream().filter(e -> !permissionIds.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            ValidationFailed event =
                new ValidationFailed("UNABLE_TO_FIND_ALL_PERMISSION_FOR_ROLE");
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        return true;
    }

    private boolean validateEndpointAndPermission(TransactionContext context) {
        //each endpoint if secured then mapped to one permission
        Set<Endpoint> securedEp = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getEndpointRepository().query(e),
                EndpointQuery.securedQuery());
        Set<PermissionId> usedPIds =
            securedEp.stream().map(Endpoint::getPermissionId).collect(Collectors.toSet());
        Set<Permission> storedP = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                PermissionQuery.internalQuery(usedPIds));
        if (storedP.size() != usedPIds.size()) {
            ValidationFailed event =
                new ValidationFailed("ENDPOINT_PERMISSION_CAN_BE_FOUND_IN_PERMISSION");
            Set<PermissionId> storedPIds =
                storedP.stream().map(Permission::getPermissionId).collect(Collectors.toSet());
            Set<PermissionId> missing =
                usedPIds.stream().filter(e -> !storedPIds.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        Set<EndpointId> permissionLinkedEpId =
            DomainRegistry.getPermissionRepository().allApiPermissionLinkedEpId();
        Set<Endpoint> endpoints = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getEndpointRepository().query(e),
                new EndpointQuery(permissionLinkedEpId));
        if (permissionLinkedEpId.size() != endpoints.size()) {

            ValidationFailed event =
                new ValidationFailed("EACH_API_PERMISSION_NEEDS_MAPPED_TO_ONE_ENDPOINT");
            Set<EndpointId> foundEpIds =
                endpoints.stream().map(Endpoint::getEndpointId).collect(Collectors.toSet());
            Set<EndpointId> missing =
                permissionLinkedEpId.stream().filter(e -> !foundEpIds.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        if (endpoints.stream().anyMatch(e -> !e.getSecured())) {
            ValidationFailed event =
                new ValidationFailed("ENDPOINT_HAS_PERMISSION_MUST_BE_SECURED");
            Set<EndpointId> missing =
                endpoints.stream().filter(e -> !e.getSecured()).map(Endpoint::getEndpointId)
                    .limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        return true;
    }

    private String convertDomainIds(Set<? extends DomainId> missing) {
        return convertToReadable(missing.stream().map(DomainId::getDomainId)
            .collect(Collectors.toSet()));
    }

    private String convertToReadable(Set<String> collect) {
        return String.join(" ", collect);//cannot use "," due to message use it as default delimiter
    }

    private boolean validateProjectAndUser(TransactionContext context) {
        //mapped user relation has valid projectId
        Set<ProjectId> usedProjectIds = DomainRegistry.getUserRelationRepository().getProjectIds();
        Set<Project> currentProjects = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getProjectRepository().query(e),
                new ProjectQuery(usedProjectIds));
        if (currentProjects.size() != usedProjectIds.size()) {

            ValidationFailed event =
                new ValidationFailed("MAPPED_USER_RELATION_HAS_VALID_PROJECT_ID");
            Set<ProjectId> stored =
                currentProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());
            Set<ProjectId> missing =
                usedProjectIds.stream().filter(e -> !stored.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        return true;
    }

    private boolean validateProjectAndRole(TransactionContext context) {
        //projectId in role table must be valid
        Set<ProjectId> usedProjectIds = DomainRegistry.getRoleRepository().getProjectIds();
        Set<Project> currentProjects = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getProjectRepository().query(e),
                new ProjectQuery(usedProjectIds));
        if (currentProjects.size() != usedProjectIds.size()) {
            ValidationFailed event =
                new ValidationFailed("PROJECT_ID_IN_ROLE_MUST_BE_VALID");
            context
                .append(event);
            Set<ProjectId> stored =
                currentProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());
            Set<ProjectId> missing =
                usedProjectIds.stream().filter(e -> !stored.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));

            return false;
        }
        //project must have project role and client_root role
        Set<ProjectId> projectIds1 = DomainRegistry.getProjectRepository().allProjectIds();
        Set<Role> roles = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                RoleQuery.projectDefaultRoleQuery());
        Set<ProjectId> missing = projectIds1.stream().filter(e -> {
            Optional<Role> first = roles.stream()
                .filter(ee -> ee.getProjectId().equals(e) && ee.getType().equals(RoleType.PROJECT))
                .findFirst();
            Optional<Role> first2 = roles.stream().filter(
                    ee -> ee.getProjectId().equals(e) && ee.getType().equals(RoleType.CLIENT_ROOT))
                .findFirst();
            return first.isEmpty() || first2.isEmpty();
        }).limit(5).collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            ValidationFailed event =
                new ValidationFailed("PROJECT_MUST_HAVE_RELATED_ROLE_CREATED");
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        return true;
    }

    private boolean validateCorsProfileAndEndpoint(TransactionContext context) {
        //endpoints must have valid cors profile
        Set<CorsProfileId> usedCorsIds =
            DomainRegistry.getEndpointRepository().getCorsProfileIds();
        Set<CorsProfile> storedCors = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getCorsProfileRepository().query(e),
                CorsProfileQuery.internalQuery(usedCorsIds));
        if (storedCors.size() != usedCorsIds.size()) {
            ValidationFailed event =
                new ValidationFailed("ENDPOINTS_MUST_HAVE_VALID_CORS_PROFILE");
            Set<CorsProfileId> stored =
                storedCors.stream().map(CorsProfile::getCorsId).collect(Collectors.toSet());
            Set<CorsProfileId> missing =
                usedCorsIds.stream().filter(e -> !stored.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        return true;
    }

    private boolean validateClientAndRole(TransactionContext context) {
        //client must have related role, role must have related client
        Set<ClientId> clientsStored = DomainRegistry.getClientRepository().allClientIds();
        Set<Role> allClientRoles = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                new RoleQuery(RoleType.CLIENT));
        Set<String> roleNames =
            allClientRoles.stream().map(Role::getName).collect(Collectors.toSet());
        if (clientsStored.stream().anyMatch(e -> !roleNames.contains(e.getDomainId()))) {
            Set<ClientId> missing =
                clientsStored.stream().filter(e -> !roleNames.contains(e.getDomainId())).limit(5)
                    .collect(Collectors.toSet());
            ValidationFailed event =
                new ValidationFailed("CLIENT_MUST_HAVE_RELATED_ROLE");
            event.addMessage(convertDomainIds(missing));
            context.append(event);
            return false;
        }
        Set<String> clientIds =
            clientsStored.stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        if (roleNames.stream().anyMatch(e -> !clientIds.contains(e))) {
            Set<String> collect =
                roleNames.stream().filter(e -> !clientIds.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            ValidationFailed event =
                new ValidationFailed("ROLE_MUST_HAVE_RELATED_CLIENT");
            event.addMessage(convertToReadable(collect));
            context.append(event);
            return false;
        }
        return true;
    }


    private boolean validateClientAndProject(TransactionContext context) {
        //all clients must have valid projectId
        Set<ProjectId> usedProjectIds = DomainRegistry.getClientRepository().getProjectIds();
        Set<Project> storedProjects = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getProjectRepository().query(e),
                new ProjectQuery(usedProjectIds));
        if (storedProjects.size() != usedProjectIds.size()) {
            ValidationFailed event =
                new ValidationFailed("ALL_CLIENTS_MUST_HAVE_VALID_PROJECT_ID");
            Set<ProjectId> storedProjectIds =
                storedProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());
            Set<ProjectId> missing =
                usedProjectIds.stream().filter(e -> !storedProjectIds.contains(e)).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertDomainIds(missing));
            context
                .append(event);
            return false;
        }
        return true;
    }

    private boolean validateRouterAndEndpoint(TransactionContext context) {
        //all endpoints must have valid routerId
        Set<RouterId> usedRouterIds = DomainRegistry.getEndpointRepository().getRouterIds();
        Set<Router> storedClients = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRouterRepository().query(e),
                new RouterQuery(usedRouterIds));
        if (storedClients.size() != usedRouterIds.size()) {
            Set<RouterId> storedRouterIds =
                storedClients.stream().map(Router::getRouterId).collect(Collectors.toSet());
            ValidationFailed event =
                new ValidationFailed("ALL_ENDPOINTS_MUST_HAVE_VALID_ROUTER_ID");
            context
                .append(event);
            Set<String> missing =
                usedRouterIds.stream().filter(e -> !storedRouterIds.contains(e))
                    .map(DomainId::getDomainId).limit(5)
                    .collect(Collectors.toSet());
            event.addMessage(convertToReadable(missing));
            return false;
        }
        return true;
    }

    private boolean validateCacheProfileAndEndpoint(TransactionContext context) {
        //endpoints must have valid cache profile
        Set<CacheProfileId> cacheProfileIds =
            DomainRegistry.getEndpointRepository().getCacheProfileIds();
        Set<CacheProfile> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getCacheProfileRepository().query(e),
                CacheProfileQuery.internalQuery(cacheProfileIds));
        if (allByQuery.size() != cacheProfileIds.size()) {
            context
                .append(new ValidationFailed("ENDPOINTS_MUST_HAVE_VALID_CACHE_PROFILE"));
            return false;
        }
        return true;
    }

    @NoArgsConstructor
    @Getter
    public static class ValidationFailed extends DomainEvent {
        public static final String SYSTEM_VALIDATION_FAILED = "system_validation_failed";
        public static final String name = "SYSTEM_VALIDATION_FAILED";
        private List<String> message;

        {
            setName(name);
            setTopic(SYSTEM_VALIDATION_FAILED);
        }

        public ValidationFailed(String message) {
            super(new AnyDomainId());
            log.debug("creating event for {}", message);
            this.message = new ArrayList<>();
            this.message.add(message);
        }

        public void addMessage(String message) {
            this.message.add(message);
        }
    }
}
