package com.mt.access.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class UtilityResource {

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
