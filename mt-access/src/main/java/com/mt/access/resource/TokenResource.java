package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.infrastructure.HttpUtility;
import java.security.Principal;
import java.util.HashMap;
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
     * token endpoint to track last login.
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
        String clientId = principal.getName();
        return ApplicationServiceRegistry.getTokenApplicationService()
            .grantToken(clientId, parameters, agentInfo, clientIpAddress);
    }

    /**
     * entry point for authorization flow.
     *
     * @param parameters request params
     * @param jwt        jwt generated
     * @return authorization flow response
     */
    @PostMapping("/authorize")
    public Map<String, String> authorize(
        @RequestParam Map<String, String> parameters,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        HashMap<String, String> response = new HashMap<>();
        String code = ApplicationServiceRegistry.getTokenApplicationService()
            .authorize(parameters);
        response.put("authorize_code", code);
        return response;
    }
}
