package com.mt.proxy.infrastructure;

import com.mt.proxy.domain.CorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {
    @Autowired
    private CustomEndpointCsrfMatcher customEndpointCsrfMatcher;
    @Autowired
    private CorsService corsService;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        CookieServerCsrfTokenRepository cookieCsrfTokenRepository =
            new CookieServerCsrfTokenRepository();
        cookieCsrfTokenRepository.setCookieHttpOnly(false);
        httpSecurity
            .authorizeExchange()
            .anyExchange().permitAll()
            .and()
            .csrf()
            .csrfTokenRepository(cookieCsrfTokenRepository)
            .requireCsrfProtectionMatcher(customEndpointCsrfMatcher)
            .and()
            .cors().configurationSource(corsService)
            .and()
            .oauth2ResourceServer().jwt()
        ;
        return httpSecurity.build();
    }

}
