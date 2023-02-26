package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.command.PermissionUpdateCommand;
import com.mt.access.application.permission.representation.PermissionCardRepresentation;
import com.mt.access.application.permission.representation.PermissionRepresentation;
import com.mt.access.application.permission.representation.UiPermissionInfo;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.infrastructure.Utility;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;
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
public class PermissionResource {

    @PostMapping(path = "projects/{projectId}/permissions")
    public ResponseEntity<Void> createForRoot(
        @PathVariable String projectId,
        @RequestBody PermissionCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getPermissionApplicationService().create(command, changeId))
            .build();
    }

    @GetMapping(path = "projects/{projectId}/permissions")
    public ResponseEntity<SumPagedRep<PermissionCardRepresentation>> readForRootByQuery(
        @PathVariable String projectId,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = Utility.updateProjectId(queryParam, projectId);
        SumPagedRep<Permission> clients =
            ApplicationServiceRegistry.getPermissionApplicationService()
                .query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(PermissionCardRepresentation
            .updateName(new SumPagedRep<>(clients, PermissionCardRepresentation::new)));
    }

    @GetMapping(path = "permissions/shared")
    public ResponseEntity<SumPagedRep<PermissionCardRepresentation>> sharedPermission(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<Permission> clients =
            ApplicationServiceRegistry.getPermissionApplicationService()
                .sharedPermissions(queryParam, pageParam);
        return ResponseEntity.ok(PermissionCardRepresentation
            .updateName(new SumPagedRep<>(clients, PermissionCardRepresentation::new)));
    }

    @GetMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<PermissionRepresentation> readForRootById(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        Optional<Permission> client =
            ApplicationServiceRegistry.getPermissionApplicationService().getById(projectId, id);
        return client.map(value -> ResponseEntity.ok(new PermissionRepresentation(value)))
            .orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable String projectId,
                                                   @PathVariable String id,
                                                   @RequestBody PermissionUpdateCommand command,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                       String changeId,
                                                   @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                       String jwt) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getPermissionApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String projectId,
                                                  @PathVariable String id,
                                                  @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                      String changeId,
                                                  @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                      String jwt) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getPermissionApplicationService()
            .remove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "permissions/ui")
    public ResponseEntity<UiPermissionInfo> getPermission(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        Set<Permission> ui = ApplicationServiceRegistry.getPermissionApplicationService().ui();
        return ResponseEntity.ok(new UiPermissionInfo(ui));
    }

    @PatchMapping(path = "projects/{projectId}/permissions/{id}",
        consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestBody JsonPatch command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getPermissionApplicationService()
            .patch(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
