package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.registry.RegistryCardRepresentation;
import com.mt.common.domain.CommonDomainRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json")
public class UtilityResource {
    @PostMapping(path = "cache/clean")
    public ResponseEntity<Void> createForApp() {
        CommonDomainRegistry.getHibernateCacheService().clearCache();
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "registry")
    public ResponseEntity<List<RegistryCardRepresentation>> registryStatus() {
        List<RegistryCardRepresentation> info = ApplicationServiceRegistry.getRegistryApplicationService().getInfo();
        RegistryCardRepresentation.updateDetails(info);
        return ResponseEntity.ok(info);
    }
}
