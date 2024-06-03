package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.representation.PermissionCardRepresentation;
import com.mt.access.application.permission.representation.UiPermissionInfo;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.infrastructure.Utility;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<Void> tenantCreate(
        @PathVariable String projectId,
        @RequestBody PermissionCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location",
                ApplicationServiceRegistry.getPermissionApplicationService()
                    .tenantCreate(command, changeId))
            .build();
    }

    @GetMapping(path = "projects/{projectId}/permissions")
    public ResponseEntity<SumPagedRep<PermissionCardRepresentation>> tenantQuery(
        @PathVariable String projectId,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = Utility.updateProjectIds(queryParam, projectId);
        SumPagedRep<Permission> rep =
            ApplicationServiceRegistry.getPermissionApplicationService()
                .tenantQuery(queryParam, pageParam, skipCount);
        SumPagedRep<PermissionCardRepresentation> sumPagedRep =
            PermissionCardRepresentation
                .updateEndpointName(new SumPagedRep<>(rep, PermissionCardRepresentation::new));
        PermissionCardRepresentation.updateLinkedEndpointName(sumPagedRep);
        return ResponseEntity.ok(PermissionCardRepresentation
            .updateProjectName(projectId, sumPagedRep));
    }

    @GetMapping(path = "projects/{projectId}/permissions/shared")
    public ResponseEntity<SumPagedRep<PermissionCardRepresentation>> sharedQuery(
        @PathVariable String projectId,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<Permission> rep =
            ApplicationServiceRegistry.getPermissionApplicationService()
                .sharedQuery(projectId, queryParam, pageParam);
        return ResponseEntity.ok(PermissionCardRepresentation
            .updateEndpointName(new SumPagedRep<>(rep, PermissionCardRepresentation::new)));
    }

    @DeleteMapping(path = "projects/{projectId}/permissions/{id}")
    public ResponseEntity<Void> tenantRemove(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getPermissionApplicationService()
            .tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "projects/{projectId}/permissions/ui")
    public ResponseEntity<UiPermissionInfo> uiQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        UiPermissionInfo ui =
            ApplicationServiceRegistry.getPermissionApplicationService().uiQuery(projectId);
        return ResponseEntity.ok(ui);
    }
}
