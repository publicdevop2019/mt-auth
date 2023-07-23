package com.mt.proxy.domain;

import static com.mt.proxy.domain.Utility.isWebSocket;

import com.mt.proxy.infrastructure.LogService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Service
public class CorsService implements CorsConfigurationSource {
    private final Map<Endpoint, CorsConfiguration> corsConfigurations = new HashMap<>();
    @Autowired
    private EndpointService endpointService;

    public void refresh(Set<Endpoint> cached) {
        log.debug("refresh cors config");
        corsConfigurations.clear();
        cached.forEach(endpoint -> {
            if (endpoint.hasCorsInfo()) {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                updateCorsConfig(corsConfiguration, endpoint);
                corsConfigurations.put(endpoint,
                    corsConfiguration);
            } else {
                corsConfigurations.put(endpoint,
                    null);
            }
        });
        log.debug("refresh cors config completed, cors configuration count is {}",
            corsConfigurations.size());
    }

    private void updateCorsConfig(CorsConfiguration configuration, Endpoint endpoint) {
        Endpoint.CorsConfig corsConfig = endpoint.getCorsConfig();
        if (corsConfig != null) {
            if (corsConfig.getOrigin().stream().anyMatch("*"::equalsIgnoreCase)) {
                configuration.addAllowedOriginPattern("*");
            } else {
                corsConfig.getOrigin().forEach(configuration::addAllowedOrigin);
            }
            configuration.setAllowCredentials(corsConfig.getCredentials());
            configuration.setAllowedHeaders(List.copyOf(corsConfig.getAllowedHeaders()));
            configuration.setExposedHeaders(List.copyOf(corsConfig.getExposedHeaders()));
            configuration.addAllowedMethod(endpoint.getMethod());
            configuration.setMaxAge(corsConfig.getMaxAge());
        }
    }

    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        String method;
        method = getTargetMethod(exchange);
        if (method == null) {
            return null;
        }
        String path = exchange.getRequest().getPath().value();
        Optional<Endpoint> endpoint = DomainRegistry.getEndpointService()
            .findEndpoint(path, method, isWebSocket(exchange.getRequest().getHeaders()));
        if (endpoint.isEmpty()) {
            LogService.reactiveLog(exchange.getRequest(),
                (ignored) -> log.debug("unable to find cors config due to missing endpoint"));
            return null;
        }
        CorsConfiguration corsConfiguration = corsConfigurations.get(endpoint.get());
        if (corsConfiguration != null) {
            LogService.reactiveLog(exchange.getRequest(),
                (ignored) -> log.debug("mismatch cors config could also result 403"));
            LogService.reactiveLog(exchange.getRequest(),
                (ignored) -> log.trace("found {} for path {} with method {}", corsConfiguration,
                    exchange.getRequest().getPath().value(),
                    exchange.getRequest().getMethodValue()));
            LogService.reactiveLog(exchange.getRequest(),
                (ignored) -> log.trace("pattern {}", corsConfiguration.getAllowedOriginPatterns()));
        }
        return corsConfiguration;
    }

    /**
     * get target method when OPTION request received
     *
     * @param exchange exchange
     * @return target method
     */
    private String getTargetMethod(ServerWebExchange exchange) {
        String targetMethod;
        if ("options".equalsIgnoreCase(exchange.getRequest().getMethodValue())) {
            if (exchange.getRequest().getHeaders().getAccessControlRequestMethod() == null) {
                LogService.reactiveLog(exchange.getRequest(),
                    (ignored) -> log.error(
                        "unexpected null value for access-control-request-method"));
                return null;
            }
            targetMethod =
                exchange.getRequest().getHeaders().getAccessControlRequestMethod().toString();
        } else {
            targetMethod = exchange.getRequest().getMethodValue();
        }
        return targetMethod;
    }
}
