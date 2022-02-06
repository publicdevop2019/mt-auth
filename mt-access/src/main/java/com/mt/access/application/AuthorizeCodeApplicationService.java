package com.mt.access.application;

import com.mt.access.application.client.ClientApplicationService;
import com.mt.access.domain.DomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class AuthorizeCodeApplicationService {

    @Autowired
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;
    @Autowired
    private ClientApplicationService clientApplicationService;

    private final OAuth2RequestValidator oauth2RequestValidator = new DefaultOAuth2RequestValidator();

    private final RedirectResolver redirectResolver = new DefaultRedirectResolver();

    public Map<String, String> authorize(Map<String, String> parameters) {

        /**make sure authorize client exist*/

        if (ApplicationServiceRegistry.getClientApplicationService().loadClientByClientId(parameters.get(OAuth2Utils.CLIENT_ID)) == null) {
            log.error("unable to find authorize client {}", parameters.get(OAuth2Utils.CLIENT_ID));
            throw new IllegalArgumentException("unable to find authorize client");
        }

        Authentication authentication = DomainRegistry.getAuthenticationService().getAuthentication();
        log.debug("before create authorization request");
        if (log.isDebugEnabled()) {
            try {
                Class<?> aClass = Class.forName(defaultOAuth2RequestFactory.getClass().getName());
                Field field = aClass.getDeclaredField("clientDetailsService");
                field.setAccessible(true);
                Object o = field.get(defaultOAuth2RequestFactory);
                log.debug("clientDetailsService is {}", o == null ? "null" : "not null");
                log.debug("clientApplicationService is {}", clientApplicationService == null ? "null" : "not null");
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);
        log.debug("after create authorization request");

        Set<String> responseTypes = authorizationRequest.getResponseTypes();

        if (!responseTypes.contains("token") && !responseTypes.contains("code"))
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);

        if (authorizationRequest.getClientId() == null)
            throw new InvalidClientException("A client id must be provided");


        ClientDetails client = ApplicationServiceRegistry.getClientApplicationService().loadClientByClientId(authorizationRequest.getClientId());

        String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
        String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
        if (!StringUtils.hasText(redirectUriParameter)) {
            throw new RedirectMismatchException(
                    "A redirectUri must be either supplied or preconfigured in the ClientDetails");
        }
        authorizationRequest.setRedirectUri(resolvedRedirect);

        oauth2RequestValidator.validateScope(authorizationRequest, client);

        authorizationRequest.setApproved(true);

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("authorize_code", generateCode(authorizationRequest, authentication));
        return stringStringHashMap;


    }

    private String generateCode(AuthorizationRequest authorizationRequest, Authentication authentication) {

        try {

            OAuth2Request storedOAuth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);

            OAuth2Authentication combinedAuth = new OAuth2Authentication(storedOAuth2Request, authentication);
            return ApplicationServiceRegistry.getRedisAuthorizationCodeServices().createAuthorizationCode(combinedAuth);

        } catch (OAuth2Exception e) {

            if (authorizationRequest.getState() != null) {
                e.addAdditionalInformation("state", authorizationRequest.getState());
            }
            throw e;
        }
    }
}
