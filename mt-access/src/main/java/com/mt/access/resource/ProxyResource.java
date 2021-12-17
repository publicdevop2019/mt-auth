package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "proxy")
public class ProxyResource {
    @GetMapping(path = "check")
    public ResponseEntity<CheckSumRepresentation> checkSync() {
        return ResponseEntity.ok(ApplicationServiceRegistry.getProxyApplicationService().checkSumValue());
    }
}
