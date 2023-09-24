package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class UserRelation extends Auditable {
    private UserId userId;
    private ProjectId projectId;

    private Set<RoleId> standaloneRoles = new HashSet<>();

    private Set<ProjectId> tenantIds = new HashSet<>();

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId, ProjectId tenantId) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles = new HashSet<>();
        this.standaloneRoles.add(roleId);
        this.tenantIds = new HashSet<>();
        this.tenantIds.add(tenantId);
        this.userId = creator;
        this.projectId = projectId;
    }

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles = new HashSet<>();
        this.standaloneRoles.add(roleId);
        this.userId = creator;
        this.projectId = projectId;
        long milli = Instant.now().toEpochMilli();
        setCreatedAt(milli);
        setCreatedBy(userId.getDomainId());
        setModifiedAt(milli);
        setModifiedBy(userId.getDomainId());
    }

    public static void onboardNewProject(RoleId adminRoleId, RoleId userRoleId, UserId creator,
                                         ProjectId tenantId, ProjectId authProjectId,
                                         TransactionContext context) {
        log.debug("start of onboarding new project");
        //to mt-auth
        Optional<UserRelation> byUserIdAndProjectId = DomainRegistry.getUserRelationRepository()
            .query(creator, authProjectId);
        UserRelation rootRelation;
        if (byUserIdAndProjectId.isPresent()) {
            rootRelation = byUserIdAndProjectId.get();
            UserRelation updated = rootRelation.updateTenantAndRole(tenantId, adminRoleId);
            DomainRegistry.getUserRelationRepository().update(rootRelation, updated);
        } else {
            rootRelation = new UserRelation(adminRoleId, creator, authProjectId, tenantId);
            DomainRegistry.getUserRelationRepository().add(rootRelation);
        }
        //to target project
        UserRelation tenantRelation = new UserRelation(userRoleId, creator, tenantId);
        DomainRegistry.getUserRelationRepository().add(tenantRelation);
        Project project = DomainRegistry.getProjectRepository().get(tenantId);
        context
            .append(new ProjectOnboardingComplete(project));
        log.debug("end of onboarding new project");
    }

    private UserRelation updateTenantAndRole(ProjectId tenantId, RoleId adminRoleId) {
        UserRelation updated =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        updated.tenantIds.add(tenantId);
        updated.standaloneRoles.add(adminRoleId);
        return updated;
    }

    public static UserRelation initNewUser(RoleId userRoleId, UserId creator,
                                           ProjectId authProjectId) {
        UserRelation userRelation2 = new UserRelation(userRoleId, creator, authProjectId);
        DomainRegistry.getUserRelationRepository().add(userRelation2);
        return userRelation2;
    }

    public static UserRelation fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                               Long modifiedAt, String modifiedBy, Integer version,
                                               ProjectId projectId, UserId userId) {
        UserRelation userRelation = new UserRelation();
        userRelation.setId(id);
        userRelation.setCreatedAt(createdAt);
        userRelation.setCreatedBy(createdBy);
        userRelation.setModifiedAt(modifiedAt);
        userRelation.setModifiedBy(modifiedBy);
        userRelation.setVersion(version);
        userRelation.projectId = projectId;
        userRelation.userId = userId;
        return userRelation;
    }

    private void setStandaloneRoles(Set<RoleId> roleIds) {
        if (Checker.notNull(roleIds)) {
            Validator.notEmpty(roleIds);
        }
        CommonUtility.updateCollection(this.standaloneRoles, roleIds,
            () -> this.standaloneRoles = roleIds);
        //TODO move this logic to custom validator
        if (Checker.notNull(roleIds)) {
            Set<Role> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                    new RoleQuery(roleIds));
            if (roleIds.size() != allByQuery.size()) {
                HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
                handler.handleError("not able to find all roles");
            }
        }
    }

    private void setTenantIds(Set<ProjectId> tenantIds) {
        if (Checker.notNull(tenantIds)) {
            Validator.notEmpty(tenantIds);
            Validator.noNullMember(tenantIds);
        }
        CommonUtility.updateCollection(this.tenantIds, tenantIds, () -> this.tenantIds = tenantIds);
    }

    public void addTenantAdmin(ProjectId tenantProjectId, RoleId tenantAdminRoleId) {
        if (getStandaloneRoles() == null) {
            HashSet<RoleId> roleIds = new HashSet<>();
            setStandaloneRoles(roleIds);
        }
        getStandaloneRoles().add(tenantAdminRoleId);
        if (getTenantIds() == null) {
            HashSet<ProjectId> projectIds = new HashSet<>();
            setTenantIds(projectIds);
        }
        getTenantIds().add(tenantProjectId);
    }

    public void removeTenantAdmin(ProjectId tenantProjectId, RoleId tenantAdminRoleId) {
        if (getStandaloneRoles() != null) {
            getStandaloneRoles().remove(tenantAdminRoleId);
        }
        if (getTenantIds() != null) {
            getTenantIds().remove(tenantProjectId);
        }
    }

    public UserRelation tenantUpdate(Set<String> roles) {
        UserRelation update =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        //TODO move to validator
        Validator.notNull(roles);
        Validator.notEmpty(roles);
        Set<RoleId> roleIds =
            roles.stream().map(RoleId::new).collect(Collectors.toSet());
        Set<Role> roleSet = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                new RoleQuery(roleIds));
        Set<ProjectId> projectIds =
            roleSet.stream().map(Role::getProjectId).collect(Collectors.toSet());
        if (projectIds.size() != 1 ||
            !projectIds.stream().findFirst().get().equals(this.projectId)) {
            throw new DefinedRuntimeException("role project id should be same", "1087",
                HttpResponseCode.BAD_REQUEST);
        }
        //remove default user so mt-auth will not be miss added to tenant list
        Set<Role> removeDefaultUser = roleSet.stream().filter(
                e -> !AppConstant.MT_AUTH_DEFAULT_USER_ROLE.equals(
                    e.getRoleId().getDomainId()))
            .collect(Collectors.toSet());
        Set<ProjectId> collect1 =
            removeDefaultUser.stream().map(Role::getTenantId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        //update tenant list based on role selected
        update.setStandaloneRoles(
            roles.stream().map(RoleId::new)
                .collect(Collectors.toSet()));
        if (collect1.isEmpty()) {
            update.setTenantIds(null);
        }
        return update;
    }

    @Override
    public String toString() {
        return CommonDomainRegistry.getCustomObjectSerializer().serialize(this);
    }

    public boolean sameAs(UserRelation updated) {
        return Objects.equals(userId, updated.userId) &&
            Objects.equals(projectId, updated.projectId) &&
            Objects.equals(standaloneRoles, updated.standaloneRoles) &&
            Objects.equals(tenantIds, updated.tenantIds);
    }

}
