package com.mt.proxy.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;
import java.util.stream.Collectors;

import static com.mt.proxy.domain.CacheService.getMostSpecificSecurityProfile;

@Slf4j
@Service
public class CorsService implements CorsConfigurationSource {
    private final Map<MethodPathKey, CorsConfiguration> corsConfigurations = new HashMap<>();
    @Autowired
    private EndpointService endpointService;

    public void refresh(Set<Endpoint> cached) {
        log.debug("refresh cors config");
        corsConfigurations.clear();
        cached.stream().filter(this::hasCorsInfo).forEach(endpoint -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            initializeCorsConfig(corsConfiguration, endpoint);
            corsConfigurations.put(new MethodPathKey(endpoint.getMethod(), endpoint.getPath()), corsConfiguration);
        });
        log.debug("refresh cors config completed, cors configuration count is {}", corsConfigurations.size());
    }

    private boolean hasCorsInfo(Endpoint e) {
        return e.getCorsConfig() != null;
    }

    private void initializeCorsConfig(CorsConfiguration configuration, Endpoint endpoint) {
        Endpoint.CorsConfig corsConfig = endpoint.getCorsConfig();
        if (corsConfig != null) {
            corsConfig.getOrigin().forEach(configuration::addAllowedOrigin);
            configuration.setAllowCredentials(corsConfig.isCredentials());
            configuration.setAllowedHeaders(List.copyOf(corsConfig.getAllowedHeaders()));
            configuration.setExposedHeaders(List.copyOf(corsConfig.getExposedHeaders()));
            configuration.addAllowedMethod(endpoint.getMethod());
            configuration.setMaxAge(corsConfig.getMaxAge());
        }
    }

    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        AntPathMatcher pathMater = endpointService.getPathMater();
        Map<MethodPathKey, CorsConfiguration> profile = new HashMap<>();
        String targetMethod;
        if ("options".equalsIgnoreCase(exchange.getRequest().getMethodValue())) {
            if (exchange.getRequest().getHeaders().getAccessControlRequestMethod() == null) {
                log.error("unexpected null value for access-control-request-method");
                return null;
            }
            targetMethod = exchange.getRequest().getHeaders().getAccessControlRequestMethod().toString();
        } else {
            targetMethod = exchange.getRequest().getMethodValue();
        }
        String finalTargetMethod = targetMethod;
        this.corsConfigurations.entrySet().stream()
                .filter(entry -> pathMater.match(entry.getKey().getPath(), exchange.getRequest().getPath().value())
                        &&
                        finalTargetMethod.equalsIgnoreCase(entry.getKey().getMethod()))
                .forEach(e -> {
                    profile.put(e.getKey(), e.getValue());
                });
        String s = exchange.getRequest().getPath().toString();
        CorsConfiguration corsConfiguration = getMostSpecificSecurityProfile(profile,s).stream().findFirst().orElse(null);
        log.debug("found {} for path {} with method {}", corsConfiguration, exchange.getRequest().getPath().value(), exchange.getRequest().getMethodValue());
        if(corsConfiguration!=null){
            log.debug("mismatch cors config could also result 403");
        }
        return corsConfiguration;
    }
}
