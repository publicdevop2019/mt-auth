package com.mt.proxy.resource;

import static org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver.X_FORWARDED_FOR;

import com.mt.proxy.domain.DomainRegistry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "info")
public class InformationResource {
    /**
     * return md5 value of current cache.
     *
     * @return md5 value
     */
    @GetMapping(path = "checkSum")
    public ResponseEntity<String> checkSync() {
        log.debug("[checking proxy md5] started");
        String check = DomainRegistry.getEndpointService().checkSumValue();
        log.debug("[checking proxy md5] completed");
        return ResponseEntity.ok(check);
    }
}
