package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.cors_profile.command.CorsProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CorsProfileUpdateCommand;
import com.mt.access.application.cors_profile.representation.CorsProfileRepresentation;
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
@RequestMapping(produces = "application/json", path = "mngmt/cors")
public class CorsProfileResource {
    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody CorsProfileCreateCommand command,
                                              @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                  String changeId) {
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getCorsProfileApplicationService().create(command, changeId))
            .build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<CorsProfileRepresentation>> readForRootByQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<CorsProfile> corsProfile =
            ApplicationServiceRegistry.getCorsProfileApplicationService()
                .corsProfile(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(corsProfile, CorsProfileRepresentation::new));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@RequestBody CorsProfileUpdateCommand command,
                                                   @PathVariable String id,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                       String changeId) {
        ApplicationServiceRegistry.getCorsProfileApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id,
                                                  @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                      String changeId) {
        ApplicationServiceRegistry.getCorsProfileApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch patch,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                     String changeId) {

        ApplicationServiceRegistry.getCorsProfileApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }
}