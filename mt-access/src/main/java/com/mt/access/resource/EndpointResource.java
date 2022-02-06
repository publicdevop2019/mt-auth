package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.endpoint.command.EndpointCreateCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.application.endpoint.representation.EndpointCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProxyCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointRepresentation;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "endpoints")
public class EndpointResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody EndpointCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getEndpointApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<?>> readForRootByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<Endpoint> endpoints = ApplicationServiceRegistry.getEndpointApplicationService().endpoints(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(endpoints, EndpointCardRepresentation::new));
    }

    @GetMapping("proxy")
    public ResponseEntity<SumPagedRep<?>> readForAppByQuery(
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<EndpointProxyCardRepresentation> endpoints = ApplicationServiceRegistry.getEndpointApplicationService().proxyEndpoints(queryParam, pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping("{id}")
    public ResponseEntity<EndpointRepresentation> readForRootById(@PathVariable String id) {
        Optional<Endpoint> endpoint = ApplicationServiceRegistry.getEndpointApplicationService().endpoint(id);
        return endpoint.map(value -> ResponseEntity.ok(new EndpointRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@RequestBody EndpointUpdateCommand command,
                                                   @PathVariable String id,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getEndpointApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getEndpointApplicationService().removeEndpoint(id, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                      @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getEndpointApplicationService().removeEndpoints(queryParam, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch patch,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {

        ApplicationServiceRegistry.getEndpointApplicationService().patchEndpoint(id, patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "event/reload")
    public ResponseEntity<Void> postForRoot(@RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getEndpointApplicationService().reloadEndpointCache(changeId);
        return ResponseEntity.ok().build();
    }
}
