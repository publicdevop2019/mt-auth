package com.mt.access.application.user_relation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.access.domain.model.user_relation.UserRelationQuery;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRelationApplicationService {
    private static final String USER_RELATION = "UserRelation";

    public Optional<UserRelation> getUserRelation(UserId userId, ProjectId projectId) {
        return DomainRegistry.getUserRelationRepository().getByUserIdAndProjectId(userId, projectId);
    }

    /**
     * create user relation to mt-auth and target project as well
     *
     * @param event
     */
    @SubscribeForEvent
    @Transactional
    public void handle(NewProjectRoleCreated event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(event.getId().toString(), (ignored) -> {
            log.debug("handle new project role created event");
            RoleId adminRoleId = new RoleId(event.getDomainId().getDomainId());
            RoleId userRoleId = event.getUserRoleId();
            UserId creator = event.getCreator();
            ProjectId tenantId = event.getProjectId();
            UserRelation.onboardNewProject(adminRoleId, userRoleId, creator, tenantId, new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
            return null;
        }, USER_RELATION);
    }

    public SumPagedRep<User> users(String projectId, String queryParam, String pageParam, String config) {
        ProjectId projectId1 = new ProjectId(projectId);
        UserRelationQuery userRelationQuery = new UserRelationQuery(projectId1, queryParam, pageParam, config);
        SumPagedRep<UserRelation> byQuery = DomainRegistry.getUserRelationRepository().getByQuery(userRelationQuery);
        Set<UserId> collect = byQuery.getData().stream().map(UserRelation::getUserId).collect(Collectors.toSet());
        UserQuery userQuery = new UserQuery(collect);
        return DomainRegistry.getUserRepository().usersOfQuery(userQuery);
    }

    public Optional<UserRelation> getUserRelationDetail(String projectId, String userId) {
        ProjectId projectId1 = new ProjectId(projectId);
        return DomainRegistry.getUserRelationRepository().getByUserIdAndProjectId(new UserId(userId), projectId1);
    }
    @SubscribeForEvent
    @Transactional
    public void replace(String projectId, String userId, UpdateUserRelationCommand command) {
        ProjectId projectId1 = new ProjectId(projectId);
        Optional<UserRelation> byUserIdAndProjectId = DomainRegistry.getUserRelationRepository().getByUserIdAndProjectId(new UserId(userId), projectId1);
        byUserIdAndProjectId.ifPresent(e->{
            e.setStandaloneRoles(command.getRoles().stream().map(RoleId::new).collect(Collectors.toSet()));
        });
    }
}
