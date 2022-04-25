package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.user.UpdateLoginInfoCommand;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenResource {
    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * override framework provided /oauth/token endpoint to track last login.
     *
     * @param principal  principal
     * @param parameters oauth2 params
     * @param servletRequest http request
     * @param agentInfo User-Agent header
     * @return oauth2 token
     * @throws HttpRequestMethodNotSupportedException method not support ex
     */
    @PostMapping(path = "/oauth/token")
    public ResponseEntity<OAuth2AccessToken> getToken(
        Principal principal,
        @RequestParam Map<String, String> parameters,
        HttpServletRequest servletRequest,
        @RequestHeader(name = "User-Agent") String agentInfo
    ) throws HttpRequestMethodNotSupportedException {
        ResponseEntity<OAuth2AccessToken> responseEntity =
            tokenEndpoint.postAccessToken(principal, parameters);
        if ("password".equalsIgnoreCase(parameters.get("grant_type"))) {
            UpdateLoginInfoCommand updateLoginInfoCommand =
                new UpdateLoginInfoCommand(servletRequest, responseEntity.getBody(), agentInfo);
            ApplicationServiceRegistry.getUserApplicationService()
                .updateLastLoginInfo(
                    updateLoginInfoCommand);
        }
        return responseEntity;
    }
}
