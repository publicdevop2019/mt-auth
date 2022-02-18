package com.mt.access.infrastructure.oauth2;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.UserApplicationService;
import com.mt.access.infrastructure.RedisAuthorizationCodeServices;
import com.mt.access.infrastructure.SelfSignedJwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    TokenStore tokenStore;

    @Autowired
    UserApplicationService userApplicationService;

    @Autowired
    JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    SelfSignedJwtTokenService authTokenHelper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RedisAuthorizationCodeServices authorizationCodeServices;

    @Autowired
    CustomTokenEnhancer customTokenEnhancer;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(ApplicationServiceRegistry.getClientApplicationService());
    }

    /**
     * explicitly set authenticationManager to enable password flow
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer, jwtAccessTokenConverter));
        endpoints
                .tokenStore(tokenStore)
                .tokenEnhancer(enhancerChain)
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices)
                .reuseRefreshTokens(false)
        ;
        authTokenHelper.setTokenGranter(endpoints.getTokenGranter());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("isAuthenticated()")
        ;
    }

    @Autowired
    public void setGlobalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userApplicationService);
    }
}
