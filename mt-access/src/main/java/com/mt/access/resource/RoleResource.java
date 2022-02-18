package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.role.command.RoleCreateCommand;
import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.application.role.representation.RoleCardRepresentation;
import com.mt.access.application.role.representation.RoleRepresentation;
import com.mt.access.domain.model.role.Role;
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
public class RoleResource {

    @PostMapping(path = "projects/{projectId}/roles")
    public ResponseEntity<Void> createForRoot(@PathVariable String projectId, @RequestBody RoleCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getRoleApplicationService().create(command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/roles")
    public ResponseEntity<SumPagedRep<RoleCardRepresentation>> readForRootByQuery(@PathVariable String projectId, @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                  @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                  @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                  @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        queryParam = Utility.updateProjectId(queryParam, projectId);
        SumPagedRep<Role> clients = ApplicationServiceRegistry.getRoleApplicationService().getByQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(RoleCardRepresentation.updateName(new SumPagedRep<>(clients, RoleCardRepresentation::new)));
    }

    @GetMapping("projects/{projectId}/roles/{id}")
    public ResponseEntity<RoleRepresentation> readForRootById(@PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Role> client = ApplicationServiceRegistry.getRoleApplicationService().getById(projectId, id);
        return client.map(value -> ResponseEntity.ok(new RoleRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("projects/{projectId}/roles/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable String projectId, @PathVariable(name = "id") String id, @RequestBody RoleUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getRoleApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/roles/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getRoleApplicationService().remove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }
}
