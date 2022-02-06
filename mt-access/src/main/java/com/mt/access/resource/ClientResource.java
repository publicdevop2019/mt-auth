package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.ClientApplicationService;
import com.mt.access.application.client.command.ClientCreateCommand;
import com.mt.access.application.client.command.ClientUpdateCommand;
import com.mt.access.application.client.representation.ClientAutoApproveRepresentation;
import com.mt.access.application.client.representation.ClientCardRepresentation;
import com.mt.access.application.client.representation.ClientProxyRepresentation;
import com.mt.access.application.client.representation.ClientRepresentation;
import com.mt.access.domain.model.client.Client;
import com.mt.access.infrastructure.JwtAuthenticationService;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "clients")
public class ClientResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody ClientCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        return ResponseEntity.ok().header("Location", clientApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<ClientCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                    @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                    @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                    @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        SumPagedRep<Client> clients = clientApplicationService().clients(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientCardRepresentation::new));
    }

    @GetMapping("proxy")
    public ResponseEntity<SumPagedRep<ClientProxyRepresentation>> internalGetAll(
                                                                                    @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                    @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount

    ) {
        SumPagedRep<Client> clients = clientApplicationService().internalClients(pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientProxyRepresentation::new));
    }

    @GetMapping("{id}")
    public ResponseEntity<ClientRepresentation> readForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        Optional<Client> client = clientApplicationService().client(id);
        return client.map(value -> ResponseEntity.ok(new ClientRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") String id, @RequestBody ClientUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        clientApplicationService().replaceClient(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        clientApplicationService().removeClient(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        clientApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("autoApprove")
    public ResponseEntity<SumPagedRep<ClientAutoApproveRepresentation>> getForUserByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                          @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                          @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                          @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        SumPagedRep<Client> clients = clientApplicationService().clients(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientAutoApproveRepresentation::new));
    }

    private ClientApplicationService clientApplicationService() {
        return ApplicationServiceRegistry.getClientApplicationService();
    }
}
