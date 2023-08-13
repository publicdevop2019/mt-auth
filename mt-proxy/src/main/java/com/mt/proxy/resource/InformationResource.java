package com.mt.proxy.resource;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.infrastructure.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<String> checkSync(ServerHttpRequest request) {
        LogService.reactiveLog(request, () -> log.debug("checking proxy md5 triggered"));
        String check = DomainRegistry.getEndpointService().checkSumValue();
        return ResponseEntity.ok(check);
    }
}
