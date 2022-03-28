package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.revoke_token.RevokeTokenCardRepresentation;
import com.mt.access.application.revoke_token.RevokeTokenCreateCommand;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "mngmt/revoke-tokens")
public class RevokeTokenResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody RevokeTokenCreateCommand command,
                                              @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
                                              @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                  String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getRevokeTokenApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<RevokeTokenCardRepresentation>> readForRootByQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<RevokeToken> endpoints =
            ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .revokeTokens(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(endpoints, RevokeTokenCardRepresentation::new));
    }
}
