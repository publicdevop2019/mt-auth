package com.mt.access.application.user_relation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRelationApplicationService {
    @Value("${mt.project.id}")
    private String authProjectId;
    private static final String USER_RELATION = "UserRelation";
    public Optional<UserRelation> getUserRelation(UserId userId, ProjectId projectId){
        return DomainRegistry.getUserRelationRepository().getByUserIdAndProjectId(userId,projectId);
    }

    /**
     * create user relation to mt-auth and target project as well
     * @param deserialize
     */
    @SubscribeForEvent
    @Transactional
    public void handle(NewProjectRoleCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle new project role created event");
            RoleId adminRoleId = new RoleId(deserialize.getDomainId().getDomainId());
            RoleId userRoleId = deserialize.getUserRoleId();
            UserId creator = deserialize.getCreator();
            Set<PermissionId> permissionIds = deserialize.getPermissionIds();
            ProjectId tenantId = deserialize.getProjectId();
            UserRelation.onboardNewProject(adminRoleId,userRoleId,creator,permissionIds,tenantId,new ProjectId(authProjectId));
            return null;
        }, USER_RELATION);
    }
}
