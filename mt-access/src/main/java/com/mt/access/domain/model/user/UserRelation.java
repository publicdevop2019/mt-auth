package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
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

    public UserRelation(UserId creator, ProjectId projectId) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
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
        } else {
            rootRelation = new UserRelation(creator, authProjectId);
            DomainRegistry.getUserRelationRepository().add(rootRelation);
        }
        UserRelationRoleId.internalAdd(rootRelation, adminRoleId);
        UserRelationTenantId.add(rootRelation, tenantId);
        //to target project
        UserRelation tenantRelation = new UserRelation(creator, tenantId);
        DomainRegistry.getUserRelationRepository().add(tenantRelation);
        UserRelationRoleId.internalAdd(tenantRelation, userRoleId);
        Project project = DomainRegistry.getProjectRepository().get(tenantId);
        context
            .append(new ProjectOnboardingComplete(project));
        log.debug("end of onboarding new project");
    }

    public static UserRelation initNewUser(RoleId userRoleId, UserId creator,
                                           ProjectId authProjectId) {
        UserRelation relation = new UserRelation(creator, authProjectId);
        DomainRegistry.getUserRelationRepository().add(relation);
        UserRelationRoleId.internalAdd(relation, userRoleId);
        return relation;
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

    public boolean sameAs(UserRelation updated) {
        return Objects.equals(userId, updated.userId) &&
            Objects.equals(projectId, updated.projectId);
    }

}
