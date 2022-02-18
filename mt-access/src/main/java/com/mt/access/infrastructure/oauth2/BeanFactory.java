package com.mt.access.infrastructure.oauth2;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.infrastructure.JwtInfoProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class BeanFactory {
    @Autowired
    JwtInfoProviderService jwtInfoProviderService;

    @Bean
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {

        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();

        handler.setTokenStore(tokenStore);

        handler.setRequestFactory(new DefaultOAuth2RequestFactory(ApplicationServiceRegistry.getClientApplicationService()));

        handler.setClientDetailsService(ApplicationServiceRegistry.getClientApplicationService());

        return handler;
    }

    @Bean
    public ApprovalStore approvalStore(TokenStore tokenStore) {

        TokenApprovalStore store = new TokenApprovalStore();

        store.setTokenStore(tokenStore);

        return store;
    }


    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {

        return new JwtTokenStore(accessTokenConverter);
    }


    @Bean
    @Primary
    public DefaultTokenServices tokenServices(TokenStore tokenStore) {

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();

        defaultTokenServices.setTokenStore(tokenStore);

        defaultTokenServices.setSupportRefreshToken(true);

        return defaultTokenServices;
    }

    @Bean
    //@Autowired is required to make sure it run after ApplicationServiceRegistry initialized
    public DefaultOAuth2RequestFactory defaultOAuth2RequestFactory(@Autowired ApplicationServiceRegistry applicationServiceRegistry) {
        log.debug("[order2] loading DefaultOAuth2RequestFactory, clientApplicationService is {}", ApplicationServiceRegistry.getClientApplicationService() == null ? "null" : "not null");
        return new DefaultOAuth2RequestFactory(ApplicationServiceRegistry.getClientApplicationService());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        Map<String, String> customHeaders =
                Collections.singletonMap("kid", "manytree-id");
        return new JwtCustomHeadersAccessTokenConverter(
                customHeaders,
                jwtInfoProviderService.getKeyPair());
    }
}
