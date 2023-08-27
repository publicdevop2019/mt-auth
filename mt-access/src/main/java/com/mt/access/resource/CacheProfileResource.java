package com.mt.access.resource;

import static com.mt.access.infrastructure.Utility.updateProjectIds;
import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.cache_profile.command.CreateCacheProfileCommand;
import com.mt.access.application.cache_profile.command.ReplaceCacheProfileCommand;
import com.mt.access.application.cache_profile.representation.CacheProfileCardRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
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
public class CacheProfileResource {
    @PostMapping(path = "projects/{projectId}/cache")
    public ResponseEntity<Void> create(
        @PathVariable String projectId,
        @RequestBody CreateCacheProfileCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getCacheProfileApplicationService()
                .tenantCreate(projectId, command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/cache")
    public ResponseEntity<SumPagedRep<CacheProfileCardRepresentation>> query(
        @PathVariable String projectId,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = updateProjectIds(queryParam, projectId);
        SumPagedRep<CacheProfileCardRepresentation> users =
            ApplicationServiceRegistry.getCacheProfileApplicationService()
                .tenantQuery(projectId, queryParam, pageParam, config);
        return ResponseEntity.ok(users);
    }

    @PutMapping(path = "projects/{projectId}/cache/{id}")
    public ResponseEntity<Void> update(
        @PathVariable String projectId,
        @RequestBody ReplaceCacheProfileCommand command,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getCacheProfileApplicationService()
            .tenantUpdate(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{projectId}/cache/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patch(
        @PathVariable String projectId,
        @PathVariable(name = "id") String id,
        @RequestBody JsonPatch command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getCacheProfileApplicationService()
            .tenantPatch(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "projects/{projectId}/cache/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getCacheProfileApplicationService()
            .tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }
}
