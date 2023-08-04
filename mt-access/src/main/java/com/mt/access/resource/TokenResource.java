package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.common.domain.model.validate.Checker;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenResource {
    private static final String[] HEADERS_TO_TRY = {
        "X-Real-IP",
        "X-Forwarded-For",
    };
    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * override framework provided /oauth/token endpoint to track last login.
     *
     * @param principal      principal
     * @param parameters     oauth2 params
     * @param servletRequest http request
     * @param agentInfo      User-Agent header
     * @return oauth2 token
     * @throws HttpRequestMethodNotSupportedException method not support ex
     */
    @PostMapping(path = "/oauth/token")
    public ResponseEntity<?> getToken(
        Principal principal,
        @RequestParam Map<String, String> parameters,
        HttpServletRequest servletRequest,
        @RequestHeader(name = "User-Agent") String agentInfo
    ) throws HttpRequestMethodNotSupportedException {
        String clientIpAddress = getClientIpAddress(servletRequest);
        log.info("user login with ip {}", clientIpAddress);
        LoginResult loginResult = ApplicationServiceRegistry.getUserApplicationService()
            .userLogin(clientIpAddress, agentInfo, parameters.get("grant_type"),
                parameters.get("username"), parameters.get("mfa_code"), parameters.get("mfa_id"));
        if (Checker.isTrue(loginResult.getAllowed())) {
            try {
                return tokenEndpoint.postAccessToken(principal, parameters);
            } catch (InvalidTokenException ex) {
                log.info("refresh token expired, no need to log details");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } catch (InvalidGrantException ex) {
                log.info("invalid grant detail: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            if (Checker.isTrue(loginResult.getInvalidMfa())) {
                return ResponseEntity.badRequest().build();
            } else {
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("message", "mfa required");
                stringStringHashMap.put("mfaId", loginResult.getMfaId().getValue());
                return ResponseEntity.ok().body(stringStringHashMap);
            }
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        log.trace("--start of get client ip address");
        request.getHeaderNames().asIterator().forEachRemaining(e -> {
            log.trace("header name [{}] and value: {}", e, request.getHeader(e));
        });
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        log.trace("--end of get client ip address");
        return request.getRemoteAddr();
    }
}
