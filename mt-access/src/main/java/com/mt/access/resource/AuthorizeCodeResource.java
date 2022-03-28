package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.infrastructure.JwtCurrentUserService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * authorize code rest endpoint.
 */
@RestController
public class AuthorizeCodeResource {
    /**
     * entry point for authorization flow.
     *
     * @param parameters request params
     * @param jwt        jwt generated
     * @return authorization flow response
     */
    @PostMapping("/authorize")
    public Map<String, String> authorize(@RequestParam Map<String, String> parameters,
                                         @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        return ApplicationServiceRegistry.getAuthorizeCodeApplicationService()
            .authorize(parameters);
    }
}
