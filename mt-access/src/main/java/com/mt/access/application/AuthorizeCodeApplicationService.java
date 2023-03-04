package com.mt.access.application;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
        //make sure authorize client exist
        if (ApplicationServiceRegistry.getClientApplicationService()
            .loadClientByClientId(parameters.get(OAuth2Utils.CLIENT_ID)) == null) {
            throw new DefinedRuntimeException(
                "unable to find authorize client " + parameters.get(OAuth2Utils.CLIENT_ID), "0005",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }

        log.debug("before create authorization request");
        AuthorizationRequest authorizationRequest =
            defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);
        log.debug("after create authorization request");

        Set<String> responseTypes = authorizationRequest.getResponseTypes();

        if (!responseTypes.contains("token") && !responseTypes.contains("code")) {
            throw new DefinedRuntimeException("unsupported response types: " + responseTypes,
                "0006",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }

        if (authorizationRequest.getClientId() == null) {
            throw new DefinedRuntimeException("a client id must be provided", "0007",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }


        ClientDetails client = ApplicationServiceRegistry.getClientApplicationService()
            .loadClientByClientId(authorizationRequest.getClientId());

        String redirectUriParameter =
            authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
        String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
        if (!StringUtils.hasText(redirectUriParameter)) {
            throw new DefinedRuntimeException(
                "a redirect uri must be either supplied or preconfigured in the client details",
                "0008",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        authorizationRequest.setRedirectUri(resolvedRedirect);

        authorizationRequest.setApproved(true);
        authorizationRequest.setScope(Collections.singleton(parameters.get("project_id")));

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        Authentication authentication = DomainRegistry.getCurrentUserService().getAuthentication();
        stringStringHashMap
            .put("authorize_code", generateCode(authorizationRequest, authentication));
        return stringStringHashMap;


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
