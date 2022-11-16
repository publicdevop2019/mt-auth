package com.mt.proxy.domain;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Service
public class CacheService {
    private final Map<MethodPathKey, CacheConfiguration> configurationMap = new HashMap<>();
    @Autowired
    EndpointService endpointService;

    public static <T> Optional<T> getMostSpecificSecurityProfile(Map<MethodPathKey, T> map,
                                                                 String requestUri) {
        Optional<T> next;
        Optional<MethodPathKey> key;
        Set<MethodPathKey> methodPathKeys = map.keySet();
        if (methodPathKeys.size() == 0) {
            next = Optional.empty();
            key = Optional.empty();
        } else if (methodPathKeys.size() == 1) {
            key = methodPathKeys.stream().findFirst();
            next = Optional.of(map.get(key.get()));
        } else {
            List<MethodPathKey> collect =
                methodPathKeys.stream().filter(e -> !e.getPath().contains("/**"))
                    .collect(Collectors.toList());
            if (collect.size() == 1) {
                key = collect.stream().findFirst();
                next = Optional.of(map.get(key.get()));
            } else {
                List<MethodPathKey> collect2 =
                    methodPathKeys.stream().filter(e -> !e.getPath().endsWith("/**"))
                        .collect(Collectors.toList());
                if (collect2.size() == 1) {
                    key = collect2.stream().findFirst();
                    next = Optional.of(map.get(key.get()));
                } else {
                    //return longest
                    Optional<MethodPathKey> first = methodPathKeys.stream()
                        .sorted((a, b) -> b.getPath().length() - a.getPath().length()).findFirst();
                    key = first;
                    next = Optional.of(map.get(key.get()));
                }
            }
        }
        if (next.isPresent()) {
            // /clients/root cannot match /clients/root/**
            if (requestUri.split("/").length != key.get().getPath().split("/").length) {
                return Optional.empty();
            }
        }
        return next;
    }

    public void refresh(Set<Endpoint> cached) {
        log.debug("refresh cache config");
        configurationMap.clear();
        cached.stream().filter(this::hasCacheInfo).forEach(endpoint -> {
            CacheConfiguration configuration = new CacheConfiguration(endpoint);
            configurationMap
                .put(new MethodPathKey(endpoint.getMethod(), endpoint.getPath()), configuration);
        });
        log.debug("refresh cache config completed, cache configuration count is {}",
            configurationMap.size());
    }

    public CacheConfiguration getCacheConfiguration(ServerWebExchange exchange, boolean isEtag) {
        AntPathMatcher pathMater = endpointService.getPathMater();
        Map<MethodPathKey, CacheConfiguration> profile = new HashMap<>();
        String finalTargetMethod = exchange.getRequest().getMethodValue();
        String path;
        if (isEtag) {
            path = exchange.getRequest().getPath().value();
        } else {
            LinkedHashSet<URI> attribute = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
            Optional<URI> lb1 =
                attribute.stream().filter(e -> !e.getScheme().equals("lb")).findFirst();
            if (lb1.isPresent()) {
                path = lb1.get().getPath();
            } else {
                return null;
            }
        }
        this.configurationMap.entrySet().stream()
            .filter(entry -> pathMater.match(entry.getKey().getPath(), path)
                &&
                finalTargetMethod.equalsIgnoreCase(entry.getKey().getMethod()))
            .forEach(e -> {
                profile.put(e.getKey(), e.getValue());
            });
        CacheConfiguration configuration =
            getMostSpecificSecurityProfile(profile, path).stream().findFirst().orElse(null);
        log.debug("found config for path {} with method {}", path,
            exchange.getRequest().getMethodValue());
        return configuration;
    }

    private boolean hasCacheInfo(Endpoint endpoint) {
        return endpoint.getCacheConfig() != null;
    }

    @Getter
    public static class CacheConfiguration {
        private final boolean allowCache;
        private final Set<String> cacheControl;

        private final Long expires;

        private final Long maxAge;

        private final Long smaxAge;

        private final String vary;

        private final boolean etag;

        private final boolean weakValidation;

        public CacheConfiguration(Endpoint endpoint) {
            this.allowCache = endpoint.getCacheConfig().isAllowCache();
            this.cacheControl = endpoint.getCacheConfig().getCacheControl();
            this.expires = endpoint.getCacheConfig().getExpires();
            this.maxAge = endpoint.getCacheConfig().getMaxAge();
            this.smaxAge = endpoint.getCacheConfig().getSmaxAge();
            this.vary = endpoint.getCacheConfig().getVary();
            this.etag = endpoint.getCacheConfig().isEtag();
            this.weakValidation = endpoint.getCacheConfig().isWeakValidation();
        }

        public String getCacheControlValue() {
            List<String> list = new ArrayList<>();
            Stream<String> stringStream =
                cacheControl.stream().filter(e -> !List.of("max-age", "s-maxage").contains(e));
            if (this.maxAge != null) {
                list.add("max-age=" + this.maxAge);
            }

            if (this.smaxAge != null) {
                list.add("s-maxage=" + this.smaxAge);
            }
            return Stream.concat(stringStream, list.stream()).collect(Collectors.joining(", "));
        }
    }
}
