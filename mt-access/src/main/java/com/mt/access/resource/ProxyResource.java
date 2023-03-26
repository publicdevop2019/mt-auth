package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class ProxyResource {
    @GetMapping(path = "mgmt/proxy/check")
    public ResponseEntity<CheckSumRepresentation> checkSum() {
        return ResponseEntity
            .ok(ApplicationServiceRegistry.getProxyApplicationService().checkSumValue());
    }
}
