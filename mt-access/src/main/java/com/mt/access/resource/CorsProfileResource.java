package com.mt.access.resource;

import static com.mt.access.infrastructure.Utility.updateProjectIds;
import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.cors_profile.command.CorsProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CorsProfileUpdateCommand;
import com.mt.access.application.cors_profile.representation.CorsProfileRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.common.domain.model.restful.SumPagedRep;
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

@RestController
@RequestMapping(produces = "application/json")
public class CorsProfileResource {

    @PostMapping(path = "projects/{projectId}/cors")
    public ResponseEntity<Void> tenantCreate(
        @PathVariable String projectId,
        @RequestBody CorsProfileCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        String s =
            ApplicationServiceRegistry.getCorsProfileApplicationService()
                .tenantCreate(projectId, command, changeId);
        return ResponseEntity.ok().header("Location", s).build();
    }

    @GetMapping(path = "projects/{projectId}/cors")
    public ResponseEntity<SumPagedRep<CorsProfileRepresentation>> tenantQuery(
        @PathVariable String projectId,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = updateProjectIds(queryParam, projectId);
        SumPagedRep<CorsProfileRepresentation> corsProfile =
            ApplicationServiceRegistry.getCorsProfileApplicationService()
                .tenantQuery(projectId,queryParam, pageParam, config);
        return ResponseEntity.ok(corsProfile);
    }

    @PutMapping(path = "projects/{projectId}/cors/{id}")
    public ResponseEntity<Void> tenantUpdate(
        @PathVariable String projectId,
        @RequestBody CorsProfileUpdateCommand command,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getCorsProfileApplicationService()
            .tenantUpdate(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "projects/{projectId}/cors/{id}")
    public ResponseEntity<Void> tenantRemove(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getCorsProfileApplicationService()
            .tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{projectId}/cors/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> tenantPatch(
        @PathVariable String projectId,
        @PathVariable(name = "id") String id,
        @RequestBody JsonPatch patch,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getCorsProfileApplicationService()
            .tenantPatch(projectId, id, patch, changeId);
        return ResponseEntity.ok().build();
    }
}
