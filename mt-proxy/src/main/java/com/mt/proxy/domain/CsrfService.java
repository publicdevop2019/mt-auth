package com.mt.proxy.domain;

import static com.mt.proxy.domain.Utility.isWebSocket;

import com.mt.proxy.infrastructure.LogService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsrfService {
    private Set<Endpoint> bypassList = new HashSet<>();

    public void refresh(Set<Endpoint> endpoints) {
        log.debug("refresh csrf config");
        bypassList.clear();
        bypassList =
            endpoints.stream().filter(e -> !Boolean.TRUE.equals(e.getCsrfEnabled()))
                .collect(Collectors.toSet());
        log.debug("refresh csrf config completed");
    }

    public boolean checkBypassCsrf(ServerHttpRequest request) {
        String path = request.getPath().value();
        String method = request.getMethodValue();
        HttpHeaders headers = request.getHeaders();
        if (isWebSocket(headers)) {
            LogService.reactiveLog(request, () -> log.debug("csrf not required for websocket"));
            return true;
        } else {
            LogService.reactiveLog(request, () -> {
                log.debug("checking csrf token for path {} method {}", path, method);
            });
            Optional<Endpoint> endpoint = DomainRegistry.getEndpointService()
                .findEndpoint(path, method, isWebSocket(headers));
            if (endpoint.isEmpty()) {
                LogService.reactiveLog(request,
                    () -> log.debug("unable to find csrf config due to missing endpoint"));
                return false;
            }
            boolean contains = bypassList.contains(endpoint.get());
            if (contains) {
                LogService.reactiveLog(request, () -> log.debug("csrf not required"));
            } else {
                LogService.reactiveLog(request, () -> log.debug("csrf required"));
            }
            return contains;
        }
    }
}
