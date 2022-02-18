package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.command.PermissionUpdateCommand;
import com.mt.access.application.permission.representation.PermissionCardRepresentation;
import com.mt.access.application.permission.representation.PermissionRepresentation;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.access.infrastructure.Utility;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class PermissionResource {

    @PostMapping(path = "projects/{projectId}/permissions")
    public ResponseEntity<Void> createForRoot(@PathVariable String projectId, @RequestBody PermissionCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getPermissionApplicationService().create(command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/permissions")
    public ResponseEntity<SumPagedRep<PermissionCardRepresentation>> readForRootByQuery(@PathVariable String projectId, @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        queryParam = Utility.updateProjectId(queryParam,projectId);
        SumPagedRep<Permission> clients = ApplicationServiceRegistry.getPermissionApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(PermissionCardRepresentation.updateName(new SumPagedRep<>(clients, PermissionCardRepresentation::new)));
    }

    @GetMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<PermissionRepresentation> readForRootById(@PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Permission> client = ApplicationServiceRegistry.getPermissionApplicationService().getById(projectId, id);
        return client.map(value -> ResponseEntity.ok(new PermissionRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable String projectId, @PathVariable String id, @RequestBody PermissionUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getPermissionApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getPermissionApplicationService().remove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{projectId}/permissions/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable String projectId, @PathVariable String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getPermissionApplicationService().patch(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
