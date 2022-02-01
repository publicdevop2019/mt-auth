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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRelationApplicationService {
    private static final String USER_RELATION = "UserRelation";

    @SubscribeForEvent
    @Transactional
    public void handle(NewProjectRoleCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle new project role created event");
            RoleId adminRoleId = new RoleId(deserialize.getDomainId().getDomainId());
            RoleId userRoleId = deserialize.getUserRoleId();
            UserId creator = deserialize.getCreator();
            Set<PermissionId> permissionIds = deserialize.getPermissionIds();
            ProjectId projectId = deserialize.getProjectId();
            UserRelation userRelation = new UserRelation(adminRoleId,userRoleId,creator,permissionIds,projectId);
            DomainRegistry.getUserRelationRepository().add(userRelation);
            return null;
        }, USER_RELATION);
    }
}
