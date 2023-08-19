package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.infrastructure.HttpUtility;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenResource {

    /**
     * override framework provided /oauth/token endpoint to track last login.
     *
     * @param parameters     oauth2 params
     * @param servletRequest http request
     * @param agentInfo      User-Agent header
     * @return oauth2 token
     */
    @PostMapping(path = "/oauth/token")
    public ResponseEntity<?> getToken(
        Principal principal,
        @RequestParam Map<String, String> parameters,
        HttpServletRequest servletRequest,
        @RequestHeader(name = "User-Agent") String agentInfo
    ) {
        String clientIpAddress = HttpUtility.getClientIpAddress(servletRequest);
        log.info("user login with ip {}", clientIpAddress);
        return ApplicationServiceRegistry.getTokenApplicationService()
            .grantToken(principal, parameters, agentInfo, clientIpAddress);
    }
}
