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
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
    //use LinkedHashSet to keep order
    private Set<RoleId> standaloneRoles = new LinkedHashSet<>();

    private Set<ProjectId> tenantIds = new HashSet<>();

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId, ProjectId tenantId) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles = new LinkedHashSet<>();
        this.standaloneRoles.add(roleId);
        this.tenantIds = new HashSet<>();
        this.tenantIds.add(tenantId);
        this.userId = creator;
        this.projectId = projectId;
    }

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles = new LinkedHashSet<>();
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
        Optional<UserRelation> userRelation = DomainRegistry.getUserRelationRepository()
            .query(creator, authProjectId);
        UserRelation rootRelation;
        if (userRelation.isPresent()) {
            rootRelation = userRelation.get();
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

    private UserRelation updateTenantAndRole(ProjectId tenantId, RoleId adminRoleId) {
        UserRelation updated =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        updated.tenantIds.add(tenantId);
        updated.standaloneRoles.add(adminRoleId);
        return updated;
    }

    private void setStandaloneRoles(Set<RoleId> roleIds) {
        if (Checker.notNull(roleIds)) {
            Validator.notEmpty(roleIds);
        }
        CommonUtility.updateCollection(this.standaloneRoles, roleIds,
            () -> this.standaloneRoles = roleIds);
        UserRelationValidator.validateAllAssignedRoles(roleIds, this);
    }

    private void setTenantIds(Set<ProjectId> tenantIds) {
        if (Checker.notNull(tenantIds)) {
            Validator.notEmpty(tenantIds);
            Validator.noNullMember(tenantIds);
        }
        CommonUtility.updateCollection(this.tenantIds, tenantIds, () -> this.tenantIds = tenantIds);
    }

    public UserRelation addTenantAdmin(ProjectId tenantProjectId, RoleId tenantAdminRoleId) {
        UserRelation update =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        if (update.getStandaloneRoles() == null) {
            HashSet<RoleId> roleIds = new HashSet<>();
            update.setStandaloneRoles(roleIds);
        }
        update.getStandaloneRoles().add(tenantAdminRoleId);
        if (update.getTenantIds() == null) {
            HashSet<ProjectId> projectIds = new HashSet<>();
            update.setTenantIds(projectIds);
        }
        update.getTenantIds().add(tenantProjectId);
        return update;
    }

    public UserRelation removeTenantAdmin(ProjectId tenantProjectId, RoleId tenantAdminRoleId) {
        UserRelation update =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        if (update.getStandaloneRoles() != null) {
            update.getStandaloneRoles().remove(tenantAdminRoleId);
        }
        if (update.getTenantIds() != null) {
            update.getTenantIds().remove(tenantProjectId);
        }
        return update;
    }

    public UserRelation assignRole(Set<String> rawRoleIds) {
        UserRelation update =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        //remove default user so mt-auth will not be miss added to tenant list
        Set<RoleId> newRoleIds =
            rawRoleIds.stream().filter(e -> !AppConstant.MAIN_USER_ROLE_ID.equals(e))
                .map(RoleId::new).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<RoleId> existingRoleIds = update.getStandaloneRoles();
        newRoleIds.addAll(existingRoleIds);
        update.setStandaloneRoles(newRoleIds);
        return update;
    }

    public UserRelation removeRole(RoleId roleId) {
        UserRelation update =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        Set<RoleId> existingRoleIds = update.getStandaloneRoles();
        Set<RoleId> newRoleIds =
            existingRoleIds.stream().filter(e -> !e.equals(roleId)).collect(Collectors.toSet());
        update.setStandaloneRoles(newRoleIds);

        //update tenant list based on role selected
        Set<Role> newRoles = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                new RoleQuery(newRoleIds));
        Set<ProjectId> projectIds =
            newRoles.stream().map(Role::getTenantId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (projectIds.isEmpty()) {
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
