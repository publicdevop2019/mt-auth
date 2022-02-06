package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.cors_profile.command.CORSProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CORSProfileUpdateCommand;
import com.mt.access.application.cors_profile.representation.CORSProfileRepresentation;
import com.mt.access.domain.model.cors_profile.CORSProfile;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "cors")
public class CORSProfileResource {
    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody CORSProfileCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getCorsProfileApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<CORSProfileRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                      @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                      @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<CORSProfile> corsProfile = ApplicationServiceRegistry.getCorsProfileApplicationService().corsProfile(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(corsProfile, CORSProfileRepresentation::new));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@RequestBody CORSProfileUpdateCommand command,
                                                   @PathVariable String id,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getCorsProfileApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getCorsProfileApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch patch,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {

        ApplicationServiceRegistry.getCorsProfileApplicationService().patch(id, patch, changeId);
        return ResponseEntity.ok().build();
    }
}
