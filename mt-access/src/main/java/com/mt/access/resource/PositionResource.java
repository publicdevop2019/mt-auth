package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.position.command.PositionCreateCommand;
import com.mt.access.application.position.command.PositionUpdateCommand;
import com.mt.access.application.position.representation.PositionCardRepresentation;
import com.mt.access.application.position.representation.PositionRepresentation;
import com.mt.access.domain.model.position.Position;
import com.mt.access.infrastructure.JwtAuthenticationService;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "positions")
public class PositionResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody PositionCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getPositionApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<PositionCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                      @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                      @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                      @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        SumPagedRep<Position> clients = ApplicationServiceRegistry.getPositionApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, PositionCardRepresentation::new));
    }

    @GetMapping("{id}")
    public ResponseEntity<PositionRepresentation> readForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        Optional<Position> client = ApplicationServiceRegistry.getPositionApplicationService().getById(id);
        return client.map(value -> ResponseEntity.ok(new PositionRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") String id, @RequestBody PositionUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getPositionApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getPositionApplicationService().remove(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getPositionApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
