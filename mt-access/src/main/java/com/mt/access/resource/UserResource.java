package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.AssignRoleCommand;
import com.mt.access.application.user.command.UpdateUserCommand;
import com.mt.access.application.user.representation.ProjectAdminRepresentation;
import com.mt.access.application.user.representation.UserCardRepresentation;
import com.mt.access.application.user.representation.UserMgmtRepresentation;
import com.mt.access.application.user.representation.UserTenantCardRepresentation;
import com.mt.access.application.user.representation.UserTenantRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.User;
import com.mt.access.infrastructure.HttpUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class UserResource {
    private static final String CONTENT_TYPE = "content-type";
    private static final String LOCATION = "Location";

    @GetMapping(path = "mgmt/users")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> mgmtQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserApplicationService()
            .query(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }


    @GetMapping("mgmt/users/{id}")
    public ResponseEntity<UserMgmtRepresentation> mgmtGet(
        @PathVariable String id
    ) {
        UserMgmtRepresentation detail =
            ApplicationServiceRegistry.getUserApplicationService().mgmtQuery(id);
        return ResponseEntity.ok(detail);
    }


    @PutMapping("mgmt/users/{id}")
    public ResponseEntity<Void> mgmtLock(
        @RequestBody UpdateUserCommand command,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().mgmtLock(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "projects/{projectId}/users")
    public ResponseEntity<SumPagedRep<UserTenantCardRepresentation>> tenantQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = HttpUtility.updateProjectIds(queryParam, projectId);
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserRelationApplicationService()
            .tenantUsers(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserTenantCardRepresentation::new));
    }

    @GetMapping(path = "projects/{projectId}/users/{id}")
    public ResponseEntity<UserTenantRepresentation> tenantGet(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        UserTenantRepresentation user =
            ApplicationServiceRegistry.getUserRelationApplicationService()
                .tenantUser(projectId, id);
        return ResponseEntity.ok(user);
    }

    /**
     * assign role to user for project.
     *
     * @param projectId project id
     * @param id        user id
     * @param jwt       jwt
     * @param command   assign command
     * @return http response 200
     */
    @PostMapping(path = "projects/{projectId}/users/{id}/roles")
    public ResponseEntity<Void> tenantRoleAssign(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody AssignRoleCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .tenantRoleAssign(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * remove role from user for project.
     *
     * @param projectId project id
     * @param id        user id
     * @param jwt       jwt
     * @param roleId    roleId
     * @return http response 200
     */
    @DeleteMapping(path = "projects/{projectId}/users/{id}/roles/{roleId}")
    public ResponseEntity<Void> tenantRoleRemove(
        @PathVariable String projectId,
        @PathVariable String id,
        @PathVariable String roleId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .tenantRoleRemove(projectId, id, roleId, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "projects/{projectId}/admins")
    public ResponseEntity<SumPagedRep<ProjectAdminRepresentation>> adminQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<ProjectAdminRepresentation> resp =
            ApplicationServiceRegistry.getUserRelationApplicationService()
                .adminQuery(pageParam, projectId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(path = "projects/{projectId}/admins/{userId}")
    public ResponseEntity<Void> addAdmin(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String userId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .addAdmin(projectId, userId, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "projects/{projectId}/admins/{userId}")
    public ResponseEntity<Void> removeAdmin(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String userId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .removeAdmin(projectId, userId, changeId);
        return ResponseEntity.ok().build();
    }
}
