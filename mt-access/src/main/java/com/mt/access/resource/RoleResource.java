package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.role.command.RoleCreateCommand;
import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.application.role.representation.RoleCardRepresentation;
import com.mt.access.application.role.representation.RoleRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.role.Role;
import com.mt.access.infrastructure.Utility;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class RoleResource {

    @PostMapping(path = "projects/{projectId}/roles")
    public ResponseEntity<Void> tenantCreate(
        @PathVariable String projectId,
        @RequestBody RoleCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location",
                ApplicationServiceRegistry.getRoleApplicationService().tenantCreate(command, changeId))
            .build();
    }

    @GetMapping(path = "projects/{projectId}/roles")
    public ResponseEntity<SumPagedRep<RoleCardRepresentation>> tenantQuery(
        @PathVariable String projectId,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = Utility.updateProjectIds(queryParam, projectId);
        SumPagedRep<RoleCardRepresentation> query =
            ApplicationServiceRegistry.getRoleApplicationService()
                .query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(query);
    }

    @GetMapping("projects/{projectId}/roles/{id}")
    public ResponseEntity<RoleRepresentation> tenantQuery(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        RoleRepresentation role =
            ApplicationServiceRegistry.getRoleApplicationService().query(projectId, id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("projects/{projectId}/roles/{id}")
    public ResponseEntity<Void> tenantUpdate(
        @PathVariable String projectId,
        @PathVariable(name = "id") String id,
        @RequestBody RoleUpdateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getRoleApplicationService().tenantUpdate(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/roles/{id}")
    public ResponseEntity<Void> tenantRemove(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getRoleApplicationService().tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }
}
