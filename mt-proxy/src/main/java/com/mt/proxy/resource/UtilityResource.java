package com.mt.proxy.resource;

import com.mt.proxy.domain.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class UtilityResource {
    @Autowired
    private InstanceInfo instanceInfo;

    /**
     * readiness check.
     *
     * @return void
     */
    @GetMapping(path = "actuator/ready")
    public ResponseEntity<Void> healthCheck() {
        log.trace("ready check triggered");
        return instanceInfo.ready() ? ResponseEntity.ok().build() : ResponseEntity.status(
            HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * mark instance service-in ready
     *
     * @return void
     */
    @PostMapping(path = "service-in/ready")
    public ResponseEntity<Void> serviceInReady() {
        instanceInfo.setAutoServiceIn(true);
        return ResponseEntity.ok().build();
    }
}
