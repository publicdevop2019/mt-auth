package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.infrastructure.JwtAuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;

@RestController
public class AuthorizeCodeResource {

    @PostMapping("/authorize")
    public Map<String, String> authorize(@RequestParam Map<String, String> parameters, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        return ApplicationServiceRegistry.getAuthorizeCodeApplicationService().authorize(parameters);
    }
}
