package com.mt.access.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.revoke_token.RevokeTokenCreateCommand;
import com.mt.access.application.revoke_token.RevokeTokenCardRepresentation;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.infrastructure.JwtAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "revoke-tokens")
public class RevokeTokenResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody RevokeTokenCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getRevokeTokenApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<RevokeTokenCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                         @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                         @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<RevokeToken> endpoints = ApplicationServiceRegistry.getRevokeTokenApplicationService().revokeTokens(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(endpoints, RevokeTokenCardRepresentation::new));
    }
}
