package com.mt.proxy.resource;

import com.mt.proxy.domain.DomainRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "info")
public class InformationResource {
    @GetMapping(path = "checkSum")
    public ResponseEntity<String> checkSync() {
        String check = DomainRegistry.getEndpointService().checkSumValue();
        return ResponseEntity.ok(check);
    }
}
