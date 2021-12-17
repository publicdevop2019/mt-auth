package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.system_role.command.CreateSystemRoleCommand;
import com.mt.access.application.system_role.command.ReplaceSystemRoleCommand;
import com.mt.access.application.system_role.representation.SystemRoleCardRepresentation;
import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.infrastructure.JwtAuthenticationService;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "system-role")
public class SystemRoleResource {
    @PostMapping
    public ResponseEntity<Void> createForApp(@RequestBody CreateSystemRoleCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getSystemRoleApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<SystemRoleCardRepresentation>> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                   @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                   @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<SystemRole> users = ApplicationServiceRegistry.getSystemRoleApplicationService().systemRoles(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, SystemRoleCardRepresentation::new));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateForAdmin(@RequestBody ReplaceSystemRoleCommand command,
                                               @PathVariable String id,
                                               @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getSystemRoleApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        ApplicationServiceRegistry.getSystemRoleApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForAdmin(
                                               @PathVariable String id,
                                               @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getSystemRoleApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }
}
