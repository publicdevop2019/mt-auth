package com.mt.access.application.token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * authorize code application service.
 */
@Slf4j
@Service
public class AuthorizeCodeApplicationService {

    private final RedirectResolver redirectResolver = new DefaultRedirectResolver();
    @Autowired
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;

    /**
     * consume authorize request.
     *
     * @param parameters request params
     * @return authorization response params
     */
    public Map<String, String> authorize(Map<String, String> parameters) {
        String clientId = parameters.get("client_id");
        String responseType = parameters.get("response_type");
        String redirectUri = parameters.get("redirect_uri");
        Validator.notNull(clientId);
        Validator.notNull(responseType);
        Validator.notNull(redirectUri);
        if (!"code".equalsIgnoreCase(responseType)) {
            throw new DefinedRuntimeException("unsupported response types: " + responseType,
                "1006",
                HttpResponseCode.BAD_REQUEST);
        }

        ClientDetails client = ApplicationServiceRegistry.getClientApplicationService()
            .loadClientByClientId(clientId);

        if (client == null) {
            throw new DefinedRuntimeException(
                "unable to find authorize client " + parameters.get(OAuth2Utils.CLIENT_ID), "1005",
                HttpResponseCode.BAD_REQUEST);
        }


        String resolvedRedirect = redirectResolver.resolveRedirect(redirectUri, client);
        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setRedirectUri(resolvedRedirect);
        authorizationRequest.setApproved(true);
        authorizationRequest.setResponseTypes(Collections.singleton(responseType));
        authorizationRequest.setClientId(clientId);
        authorizationRequest.setScope(Collections.singleton(parameters.get("project_id")));

        HashMap<String, String> returnParams = new HashMap<>();
        Authentication authentication = DomainRegistry.getCurrentUserService().getAuthentication();
        returnParams
            .put("authorize_code", generateCode(authorizationRequest, authentication));
        return returnParams;


    }

    private String generateCode(AuthorizationRequest authorizationRequest,
                                Authentication authentication) {

        try {

            OAuth2Request storedOAuth2Request =
                defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);

            OAuth2Authentication combinedAuth =
                new OAuth2Authentication(storedOAuth2Request, authentication);
            return ApplicationServiceRegistry.getRedisAuthorizationCodeServices()
                .createAuthorizationCode(combinedAuth);

        } catch (OAuth2Exception e) {

            if (authorizationRequest.getState() != null) {
                e.addAdditionalInformation("state", authorizationRequest.getState());
            }
            throw e;
        }
    }
}
