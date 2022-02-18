package com.mt.access.resource;

import com.mt.common.domain.CommonDomainRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class UtilityResource {
    @PostMapping(path = "cache/clean")
    public ResponseEntity<Void> createForApp() {
        CommonDomainRegistry.getHibernateCacheService().clearCache();
        return ResponseEntity.ok().build();
    }
}
