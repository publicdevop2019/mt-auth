package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.registry.RegistryCardRepresentation;
import com.mt.common.domain.CommonDomainRegistry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class UtilityResource {

    /**
     * get eureka registry information.
     *
     * @return registry info
     */
    @GetMapping(path = "registry")
    public ResponseEntity<List<RegistryCardRepresentation>> registryInfo() {
        List<RegistryCardRepresentation> info =
            ApplicationServiceRegistry.getRegistryApplicationService().getInfo();
        return ResponseEntity.ok(info);
    }

    /**
     * do nothing api, used to attach csrf cookie in response by mt-proxy.
     *
     * @return void
     */
    @GetMapping(path = "csrf")
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.ok().build();
    }

    /**
     * do nothing api, used to check if token expire,
     * if yes, will trigger redirect to login page.
     *
     * @return void
     */
    @GetMapping(path = "expire/check")
    public ResponseEntity<Void> expireCheck() {
        return ResponseEntity.ok().build();
    }

    /**
     * ribbon health check.
     *
     * @return void
     */
    @GetMapping(path = "health")
    public ResponseEntity<Void> healthCheck() {
        log.trace("health check triggered");
        return ResponseEntity.ok().build();
    }
}
