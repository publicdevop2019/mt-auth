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

    public boolean checkAccess(ServerHttpRequest request, @Nullable String authHeader,
                               Boolean webSocket) {
        String requestUri = request.getPath().toString();
        String method = request.getMethod().name();
        if (Boolean.TRUE.equals(webSocket)) {
            if (authHeader == null) {
                LogService.reactiveLog(request,
                    () -> log.debug("check failure due to empty auth info"));
                return false;
            }
            if (!DomainRegistry.getJwtService().verify(authHeader.replace("Bearer ", ""))) {
                LogService.reactiveLog(request,
                    () -> log.debug("check failure due to jwt failed for verification"));
                return false;
            } else {
                //check roles
                try {
                    return checkAccessByPermissionId(requestUri, method, authHeader, true, request);
                } catch (ParseException e) {
                    LogService.reactiveLog(request,
                        () -> {
                            log.error("error during parse", e);
                            log.debug("check failure due to parse error");
                        });
                    return false;
                }
            }
        }
        if (requestUri.contains("/oauth/token") || requestUri.contains("/oauth/token_key")) {
            //permit all token endpoints,
            return true;
        } else if (authHeader == null || !authHeader.contains("Bearer")) {
            if (cached.size() == 0) {
                LogService.reactiveLog(request,
                    () -> log.debug("check failure due to cached endpoints are empty"));
                return false;
            }
            List<Endpoint> collect1 = cached.stream().filter(e -> !e.getSecured()).filter(
                    e -> antPathMatcher.match(e.getPath(), requestUri) && method.equals(e.getMethod()))
                .collect(Collectors.toList());
            if (collect1.size() == 0) {
                LogService.reactiveLog(request,
                    () -> log.debug(
                        "check failure due to un-registered public "
                            +
                            "endpoints or no authentication info found"));

                return false;
            } else {
                return true;
            }
        } else if (authHeader.contains("Bearer")) {
            try {
                return checkAccessByPermissionId(requestUri, method, authHeader, false, request);
            } catch (ParseException e) {
                LogService.reactiveLog(request,
                    () -> {
                        log.error("error during parse", e);
                        log.debug("check failure due to parse error");
                    });

                return false;
            }
        } else {
            LogService.reactiveLog(request,
                () -> log.debug("return 403 due to un-registered endpoints"));
            return false;
        }
    }

    private boolean checkAccessByPermissionId(String requestUri, String method, String authHeader,
                                              boolean websocket, ServerHttpRequest request)
        throws ParseException {
        //check endpoint url, method first then check resourceId and security rule
        String jwtRaw = authHeader.replace("Bearer ", "");
        Set<String> resourceIds = DomainRegistry.getJwtService().getResourceIds(jwtRaw);

        //fetch endpoint
        if (resourceIds == null || resourceIds.isEmpty()) {
            LogService.reactiveLog(request,
                () -> {
                    log.debug("return 403 due to resourceIds is null or empty");
                });

            return false;
        }
        Set<Endpoint> sameResourceId =
            cached.stream().filter(e -> resourceIds.contains(e.getResourceId()))
                .collect(Collectors.toSet());
        Optional<Endpoint> endpoint = findEndpoint(sameResourceId, requestUri, method, websocket);
        boolean passed;
        if (endpoint.isPresent()) {
            passed = endpoint.get().allowAccess(jwtRaw, log, request);
        } else {
            LogService.reactiveLog(request,
                () -> log.debug(
                    "return 403 due to endpoint not found or duplicate endpoints"));
            return false;
        }
        if (!passed) {
            LogService.reactiveLog(request,
                () -> log.debug("return 403 due to not pass check"));
            return false;
        } else {
            return true;
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
