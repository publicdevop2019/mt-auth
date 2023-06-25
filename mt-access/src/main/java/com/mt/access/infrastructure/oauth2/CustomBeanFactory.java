package com.mt.access.infrastructure.oauth2;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

/**
 * config BeanFactory.
 */
@Slf4j
@Component
public class CustomBeanFactory {
    private static final Integer STRENGTH = 12;
    /**
     * configuration.
     *
     * @param tokenStore default token store
     * @return handler
     */
    @Bean
    public TokenStoreUserApprovalHandler userApprovalHandler(
        TokenStore tokenStore,
        @Autowired
        ClientDetailsService clientDetailsService
    ) {

        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();

        handler.setTokenStore(tokenStore);

        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));

        handler.setClientDetailsService(clientDetailsService);

        return handler;
    }

    /**
     * configuration.
     *
     * @param tokenStore default token store
     * @return handler
     */
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

    /**
     * configuration to enable refresh token.
     *
     * @param tokenStore default token store
     * @return handler
     */
    @Bean
    @Primary
    public DefaultTokenServices tokenServices(TokenStore tokenStore) {

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();

        defaultTokenServices.setTokenStore(tokenStore);

        defaultTokenServices.setSupportRefreshToken(true);

        return defaultTokenServices;
    }

    @Bean
    public DefaultOAuth2RequestFactory defaultOAuth2RequestFactory(
        @Autowired ClientDetailsService clientDetailsService
    ) {
        log.debug("[order2] loading DefaultOAuth2RequestFactory, clientApplicationService is {}",
            clientDetailsService == null ? "null" : "not null");
        return new DefaultOAuth2RequestFactory(
            clientDetailsService);
    }

    /**
     * update default jwt header.
     *
     * @return converter
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter(@Autowired KeyPair keyPair) {
        Map<String, String> customHeaders =
            Collections.singletonMap("kid", "manytree-id");
        return new JwtCustomHeadersAccessTokenConverter(
            customHeaders,
            keyPair);
    }

    @Bean
    private KeyPair keyPair(@Autowired Environment env) {
        KeyStoreKeyFactory keyStoreKeyFactory =
            new KeyStoreKeyFactory(
                new ClassPathResource(Objects.requireNonNull(env.getProperty("jwt.key-store"))),
                Objects.requireNonNull(env.getProperty("jwt.password")).toCharArray());

        return keyStoreKeyFactory.getKeyPair(env.getProperty("jwt.alias"));
    }

    @Bean
    private JWKSet jwkSet(@Autowired KeyPair keyPair) {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID("manytree-id");
        return new JWKSet(builder.build());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }
}
