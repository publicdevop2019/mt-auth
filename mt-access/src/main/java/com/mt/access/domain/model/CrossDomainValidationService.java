package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleType;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.CleanUpThreadPoolExecutor;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class CrossDomainValidationService {
    @Autowired
    CleanUpThreadPoolExecutor taskExecutor;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Scheduled(fixedRate = 1 * 60 * 1000, initialDelay = 60 * 1000)
    public void validate() {
        taskExecutor.execute(() -> CommonDomainRegistry.getSchedulerDistLockService()
            .executeIfLockSuccess("validation_task", 15, (nullValue) -> {
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                template.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(
                        TransactionStatus transactionStatus) {
                        log.debug("start of validation existing data");
                        validateCacheProfileAndEndpoint();
                        validateClientAndEndpoint();
                        validateClientAndProject();
                        validateClientAndRole();
                        validateCorsProfileAndEndpoint();
                        validateProjectAndRole();
                        validateProjectAndUser();
                        validateEndpointAndPermission();
                        validateRoleAndPermission();
                        CommonApplicationServiceRegistry.getJobApplicationService()
                            .createOrUpdateJob(JobDetail.dataValidation());
                        log.debug("end of validation existing data");
                    }
                });
            }));
    }

    /**
     * make sure role's permission id exit.
     */
    private void validateRoleAndPermission() {
        Set<Role> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery(e), RoleQuery.all());
        Set<PermissionId> collect =
            allByQuery.stream().flatMap(e -> e.getTotalPermissionIds().stream())
                .collect(Collectors.toSet());
        Set<PermissionId> permissionIds =
            DomainRegistry.getPermissionRepository().allPermissionId();
        if (collect.stream().anyMatch(e -> !permissionIds.contains(e))) {
            Set<PermissionId> collect1 = collect.stream().filter(e -> !permissionIds.contains(e))
                .collect(Collectors.toSet());
            log.debug("unable to find permission ids {} for role", collect1);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("UNABLE_TO_FIND_ALL_PERMISSION_FOR_ROLE"));
        }
    }

    private void validateEndpointAndPermission() {
        //each endpoint if secured then mapped to one permission
        Set<Endpoint> securedEp = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e),
                EndpointQuery.securedQuery());
        Set<PermissionId> mappedPId =
            securedEp.stream().map(Endpoint::getPermissionId).collect(Collectors.toSet());
        Set<Permission> storedP = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery(e),
                new PermissionQuery(mappedPId));
        if (storedP.size() != mappedPId.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(
                    new ValidationFailedEvent("ENDPOINT_PERMISSION_CAN_BE_FOUND_IN_PERMISSION"));
        }
        Set<EndpointId> endpointIds =
            DomainRegistry.getPermissionRepository().allApiPermissionLinkedEpId();
        Set<Endpoint> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e),
                new EndpointQuery(endpointIds));
        if (endpointIds.size() != allByQuery.size()) {
            Set<EndpointId> foundEpIds =
                allByQuery.stream().map(Endpoint::getEndpointId).collect(Collectors.toSet());
            Set<EndpointId> missingEpIds = endpointIds.stream().filter(e -> !foundEpIds.contains(e))
                .collect(Collectors.toSet());
            log.debug("unable to find endpoint of ids {}", missingEpIds);
            CommonDomainRegistry.getDomainEventRepository().append(
                new ValidationFailedEvent("EACH_API_PERMISSION_NEEDS_MAPPED_TO_ONE_ENDPOINT"));
        }
        if (allByQuery.stream().anyMatch(e -> !e.isSecured())) {
            Set<EndpointId> publicEpIds =
                allByQuery.stream().filter(e -> !e.isSecured()).map(Endpoint::getEndpointId)
                    .collect(Collectors.toSet());
            log.debug("unable to find endpoint of ids {}", publicEpIds);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("ENDPOINT_HAS_PERMISSION_MUST_BE_SECURED"));
        }

    }

    private void validateProjectAndUser() {
        //mapped user relation has valid projectId
        Set<ProjectId> projectIds = DomainRegistry.getUserRelationRepository().getProjectIds();
        Set<Project> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery(e),
                new ProjectQuery(projectIds));
        if (allByQuery.size() != projectIds.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("MAPPED_USER_RELATION_HAS_VALID_PROJECT_ID"));
        }
    }

    private void validateProjectAndRole() {
        //projectId in role table must be valid
        Set<ProjectId> projectIds = DomainRegistry.getRoleRepository().getProjectIds();
        Set<Project> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery(e),
                new ProjectQuery(projectIds));
        if (allByQuery.size() != projectIds.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("PROJECT_ID_IN_ROLE_MUST_BE_VALID"));
        }
        //project must have project role and client_root role
        Set<ProjectId> projectIds1 = DomainRegistry.getProjectRepository().allProjectIds();
        Set<Role> roles = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery(e),
                RoleQuery.projectDefaultRoleQuery());
        Set<ProjectId> collect = projectIds1.stream().filter(e -> {
            Optional<Role> first = roles.stream()
                .filter(ee -> ee.getProjectId().equals(e) && ee.getType().equals(RoleType.PROJECT))
                .findFirst();
            Optional<Role> first2 = roles.stream().filter(
                ee -> ee.getProjectId().equals(e) && ee.getType().equals(RoleType.CLIENT_ROOT))
                .findFirst();
            return first.isEmpty() || first2.isEmpty();
        }).collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            log.debug("project must have related role created {}", collect);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("PROJECT_MUST_HAVE_RELATED_ROLE_CREATED"));
        }
    }

    private void validateCorsProfileAndEndpoint() {
        //endpoints must have valid cors profile
        Set<CorsProfileId> corsProfileIds =
            DomainRegistry.getEndpointRepository().getCorsProfileIds();
        Set<CorsProfile> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getCorsProfileRepository().corsProfileOfQuery(e),
                new CorsProfileQuery(corsProfileIds));
        if (allByQuery.size() != corsProfileIds.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("ENDPOINTS_MUST_HAVE_VALID_CORS_PROFILE"));
        }
    }

    private void validateClientAndRole() {
        //client must have related role, role must have related client
        Set<ClientId> clients = DomainRegistry.getClientRepository().allClientIds();
        DomainRegistry.getRoleRepository().getByQuery(new RoleQuery(RoleType.CLIENT));
        Set<Role> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery(e),
                new RoleQuery(RoleType.CLIENT));
        Set<String> names = allByQuery.stream().map(Role::getName).collect(Collectors.toSet());

        if (clients.stream().anyMatch(e -> !names.contains(e.getDomainId()))) {
            Set<ClientId> collect = clients.stream().filter(e -> !names.contains(e.getDomainId()))
                .collect(Collectors.toSet());
            log.debug("unable to find roles for clients {}", collect);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("CLIENT_MUST_HAVE_RELATED_ROLE"));
        }
        Set<String> clientIds =
            clients.stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        if (names.stream().anyMatch(e -> !clientIds.contains(e))) {
            Set<String> collect =
                names.stream().filter(e -> !clientIds.contains(e)).collect(Collectors.toSet());
            log.debug("unable to find client for role {}", collect);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("ROLE_MUST_HAVE_RELATED_CLIENT"));
        }
    }

    private void validateClientAndProject() {
        //all clients must have valid projectId
        Set<ProjectId> projectIds = DomainRegistry.getClientRepository().getProjectIds();
        Set<Project> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery(e),
                new ProjectQuery(projectIds));
        if (allByQuery.size() != projectIds.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("ALL_CLIENTS_MUST_HAVE_VALID_PROJECT_ID"));
        }
    }

    private void validateClientAndEndpoint() {
        //all endpoints must have valid clientId
        Set<ClientId> clientIds = DomainRegistry.getEndpointRepository().getClientIds();
        Set<Client> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getClientRepository().clientsOfQuery(e),
                new ClientQuery(clientIds));
        if (allByQuery.size() != clientIds.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("ALL_ENDPOINTS_MUST_HAVE_VALID_CLIENT_ID"));
        }
    }

    private void validateCacheProfileAndEndpoint() {
        //endpoints must have valid cache profile
        Set<CacheProfileId> cacheProfileIds =
            DomainRegistry.getEndpointRepository().getCacheProfileIds();
        Set<CacheProfile> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getCacheProfileRepository().cacheProfileOfQuery(e),
                new CacheProfileQuery(cacheProfileIds));
        if (allByQuery.size() != cacheProfileIds.size()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ValidationFailedEvent("ENDPOINTS_MUST_HAVE_VALID_CACHE_PROFILE"));
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class ValidationFailedEvent extends DomainEvent {
        public static final String SYSTEM_VALIDATION_FAILED = "system_validation_failed";
        public static final String name = "SYSTEM_VALIDATION_FAILED";
        private String message;

        public ValidationFailedEvent(String message) {
            log.debug("creating event for {}", message);
            this.message = message;
            setName(name);
            setTopic(SYSTEM_VALIDATION_FAILED);
        }
    }
}
