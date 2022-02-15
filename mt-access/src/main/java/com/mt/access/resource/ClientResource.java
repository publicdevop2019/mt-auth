package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.command.ClientCreateCommand;
import com.mt.access.application.client.command.ClientUpdateCommand;
import com.mt.access.application.client.representation.ClientAutoApproveRepresentation;
import com.mt.access.application.client.representation.ClientCardRepresentation;
import com.mt.access.application.client.representation.ClientProxyRepresentation;
import com.mt.access.application.client.representation.ClientRepresentation;
import com.mt.access.domain.model.client.Client;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.access.infrastructure.Utility.updateProjectId;
import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class ClientResource {

    @PostMapping(path = "projects/{projectId}/clients")
    public ResponseEntity<Void> createForRoot(
            @PathVariable String projectId,
            @RequestBody ClientCreateCommand command,
            @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getClientApplicationService().tenantCreate(command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/clients")
    public ResponseEntity<SumPagedRep<ClientCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                    @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                    @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                    @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
                                                                                    @PathVariable String projectId
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        queryParam = updateProjectId(queryParam, projectId);
        SumPagedRep<Client> clients = ApplicationServiceRegistry.getClientApplicationService().tenantQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientCardRepresentation::new));
    }

    @GetMapping(path = "mngmt/clients")
    public ResponseEntity<SumPagedRep<ClientCardRepresentation>> readForRootByQuery2(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                     @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                     @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                     @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
                                                                                     ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        SumPagedRep<Client> clients = ApplicationServiceRegistry.getClientApplicationService().adminQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientCardRepresentation::new));
    }
    @GetMapping("mngmt/clients/{id}")
    public ResponseEntity<ClientRepresentation> readForRootById2(@PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Client> client = ApplicationServiceRegistry.getClientApplicationService().adminQuery(id);
        return client.map(value -> ResponseEntity.ok(new ClientRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    //for internal proxy to create router
    @GetMapping(path = "clients/proxy")
    public ResponseEntity<SumPagedRep<ClientProxyRepresentation>> internalGetAll(
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount

    ) {
        SumPagedRep<Client> clients = ApplicationServiceRegistry.getClientApplicationService().internalQuery(pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientProxyRepresentation::new));
    }

    @GetMapping("projects/{projectId}/clients/{id}")
    public ResponseEntity<ClientRepresentation> readForRootById(@PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Client> client = ApplicationServiceRegistry.getClientApplicationService().tenantQuery(id, projectId);
        return client.map(value -> ResponseEntity.ok(new ClientRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("projects/{projectId}/clients/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable String projectId, @PathVariable(name = "id") String id, @RequestBody ClientUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getClientApplicationService().tenantReplace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/clients/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String projectId, @PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getClientApplicationService().tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{projectId}/clients/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable String projectId, @PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getClientApplicationService().patch(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("projects/{projectId}/clients/autoApprove")
    public ResponseEntity<SumPagedRep<ClientAutoApproveRepresentation>> getForUserByQuery(@PathVariable String projectId, @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                          @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                          @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                          @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        queryParam = updateProjectId(queryParam, projectId);
        SumPagedRep<Client> clients = ApplicationServiceRegistry.getClientApplicationService().tenantQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientAutoApproveRepresentation::new));
    }

}
