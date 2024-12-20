package com.mt.proxy.domain;

import static com.mt.proxy.domain.Utility.antPathMatcher;

import com.mt.proxy.infrastructure.LogService;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EndpointService {

    private Set<Endpoint> cached = new LinkedHashSet<>();

    /**
     * return most specific endpoint from multiple endpoints.
     * e.g /** vs /**\/**
     *
     * @param endpoints  list of endpoints
     * @param requestUri path
     * @return optional most specific endpoint
     */
    private static Optional<Endpoint> getClosestEndpoint(List<Endpoint> endpoints,
                                                         String requestUri) {
        Optional<Endpoint> next;
        if (endpoints.size() == 1) {
            next = Optional.of(endpoints.get(0));
        } else {
            List<Endpoint> exactMatch = endpoints.stream().filter(e -> !e.getPath().contains("/**"))
                .collect(Collectors.toList());
            if (exactMatch.size() == 1) {
                next = Optional.of(exactMatch.get(0));
            } else {
                List<Endpoint> collect2 =
                    endpoints.stream().filter(e -> !e.getPath().endsWith("/**"))
                        .collect(Collectors.toList());
                if (collect2.size() == 1) {
                    next = Optional.of(collect2.get(0));
                } else {
                    //return longest
                    next = endpoints.stream()
                        .sorted((a, b) -> b.getPath().length() - a.getPath().length()).findFirst();
                }
            }
        }
        if (next.isPresent()) {
            // /clients/root cannot match /clients/root/**
            if (requestUri.split("/").length != next.get().getPath().split("/").length) {
                return Optional.empty();
            }
        }
        return next;
    }

    public void refreshCache() {
        cached = DomainRegistry.getRetrieveEndpointService().loadAllEndpoints();
        log.info("total endpoints retrieved {}", cached.size());
        DomainRegistry.getCsrfService().refresh(cached);
        DomainRegistry.getCorsService().refresh(cached);
        DomainRegistry.getCacheService().refresh(cached);
    }

    public EndpointCheckResult checkAccess(ServerHttpRequest request, @Nullable String authHeader,
                                           Boolean webSocket) {
        String requestUri = request.getPath().toString();
        String method = request.getMethod().name();
        if (Boolean.TRUE.equals(webSocket)) {
            if (authHeader == null) {
                return EndpointCheckResult.emptyAuth();
            }
            if (!DomainRegistry.getJwtService().verify(authHeader.replace("Bearer ", ""))) {
                return EndpointCheckResult.invalidJwt();
            } else {
                //check permissions
                try {
                    return checkAccessByPermissionId(requestUri, method, authHeader, true);
                } catch (ParseException e) {
                    LogService.reactiveLog(request,
                        () -> log.error("error during parse", e));
                    return EndpointCheckResult.parseError();
                }
            }
        }
        if (authHeader == null || !authHeader.contains("Bearer")) {
            if (cached.size() == 0) {
                return EndpointCheckResult.emptyCache();
            }
            List<Endpoint> collect1 = cached.stream().filter(e -> !e.getSecured()).filter(
                    e -> antPathMatcher.match(e.getPath(), requestUri) && method.equals(e.getMethod()))
                .collect(Collectors.toList());
            if (collect1.size() == 0) {
                return EndpointCheckResult.unregisterPublicOrNoAuth();
            } else {
                return EndpointCheckResult.allow();
            }
        } else if (authHeader.contains("Bearer")) {
            if (!DomainRegistry.getJwtService().verify(authHeader.replace("Bearer ", ""))) {
                return EndpointCheckResult.invalidJwt();
            }
            try {
                return checkAccessByPermissionId(requestUri, method, authHeader, false);
            } catch (ParseException e) {
                LogService.reactiveLog(request,
                    () -> log.error("error during parse", e));

                return EndpointCheckResult.parseError();
            }
        } else {
            return EndpointCheckResult.unregister();
        }
    }

    private EndpointCheckResult checkAccessByPermissionId(String requestUri, String method,
                                                          String authHeader,
                                                          boolean websocket)
        throws ParseException {
        //check endpoint url, method first then check resourceId and security rule
        String jwtRaw = authHeader.replace("Bearer ", "");
        Set<String> resourceIds = DomainRegistry.getJwtService().getResourceIds(jwtRaw);

        //fetch endpoint
        if (resourceIds == null || resourceIds.isEmpty()) {
            return EndpointCheckResult.missingResourceId();
        }
        Set<Endpoint> sameResourceId =
            cached.stream().filter(e -> resourceIds.contains(e.getResourceId()))
                .collect(Collectors.toSet());
        Optional<Endpoint> endpoint = findEndpoint(sameResourceId, requestUri, method, websocket);
        if (endpoint.isPresent()) {
            return endpoint.get().checkAccess(jwtRaw);
        } else {
            return EndpointCheckResult.notFoundOrDuplicate();
        }
    }

    public String checkSumValue() {
        return DomainRegistry.getCheckSumService().getChecksum(cached);
    }

    /**
     * return matching endpoint info from given endpoint collection or cache
     *
     * @param cache      endpoints to search, if null will use cached endpoints
     * @param requestUri url path
     * @param method     method
     * @param websocket  if websocket
     * @return optional matching endpoint object
     */
    public Optional<Endpoint> findEndpoint(@Nullable Set<Endpoint> cache,
                                           String requestUri, String method,
                                           boolean websocket) {
        Set<Endpoint> from;
        from = cache == null ? cached : cache;
        //fetch security rule by endpoint & method
        List<Endpoint> next;
        if (websocket) {
            next = from.stream()
                .filter(e -> antPathMatcher.match(e.getPath(), requestUri) && e.getWebsocket())
                .collect(Collectors.toList());
        } else {
            next = from.stream().filter(
                    e -> antPathMatcher.match(e.getPath(), requestUri) && method.equals(e.getMethod()))
                .collect(Collectors.toList());
        }
        return getClosestEndpoint(next, requestUri);
    }

    /**
     * return matching endpoint info from cached endpoint collection
     *
     * @param requestUri url path
     * @param method     method
     * @param websocket  if websocket
     * @return optional matching endpoint object
     */
    public Optional<Endpoint> findEndpoint(String requestUri, String method, boolean websocket) {
        return findEndpoint(null, requestUri, method, websocket);
    }

    /**
     * return cached endpoint collection.
     *
     * @return endpoint set
     */
    public Set<Endpoint> getCachedEndpoints() {
        return cached;
    }
}
