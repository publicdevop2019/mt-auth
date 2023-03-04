package com.mt.access.resource;

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
import com.mt.access.domain.model.cache_profile.CacheProfile;
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
@RequestMapping(produces = "application/json", path = "mngmt/cache-profile")
public class CacheProfileResource {
    @PostMapping
    public ResponseEntity<Void> createForApp(@RequestBody CreateCacheProfileCommand command,
                                             @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                             String changeId) {
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getCacheProfileApplicationService()
                .create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<CacheProfileCardRepresentation>> readForAdminByQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<CacheProfile> users =
            ApplicationServiceRegistry.getCacheProfileApplicationService()
                .cacheProfiles(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, CacheProfileCardRepresentation::new));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateForAdmin(@RequestBody ReplaceCacheProfileCommand command,
                                               @PathVariable String id,
                                               @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                               String changeId) {
        ApplicationServiceRegistry.getCacheProfileApplicationService()
            .update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch command,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                 String changeId,
                                                 @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                 String jwt) {
        ApplicationServiceRegistry.getCacheProfileApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForAdmin(
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getCacheProfileApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }
}
