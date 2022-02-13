package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.organization.command.OrganizationCreateCommand;
import com.mt.access.application.organization.command.OrganizationUpdateCommand;
import com.mt.access.application.organization.representation.OrganizationCardRepresentation;
import com.mt.access.application.organization.representation.OrganizationRepresentation;
import com.mt.access.domain.model.organization.Organization;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "organizations")
public class OrganizationResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody OrganizationCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getOrganizationApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<OrganizationCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                          @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                          @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                          @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        SumPagedRep<Organization> clients = ApplicationServiceRegistry.getOrganizationApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, OrganizationCardRepresentation::new));
    }

    @GetMapping("{id}")
    public ResponseEntity<OrganizationRepresentation> readForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Organization> client = ApplicationServiceRegistry.getOrganizationApplicationService().getById(id);
        return client.map(value -> ResponseEntity.ok(new OrganizationRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") String id, @RequestBody OrganizationUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getOrganizationApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getOrganizationApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getOrganizationApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
