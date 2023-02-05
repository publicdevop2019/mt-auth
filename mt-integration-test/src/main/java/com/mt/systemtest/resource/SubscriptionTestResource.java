package com.mt.systemtest.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionTestResource {
    @GetMapping("internal/not/shared")
    public ResponseEntity<?> internalNotShared() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("internal/shared")
    public ResponseEntity<?> internalShared() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("external/shared/no/auth")
    public ResponseEntity<?> externalSharedNoAuth() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("external/shared/auth")
    public ResponseEntity<?> externalSharedAuth() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("external/not/shared/auth")
    public ResponseEntity<?> externalNotSharedAuth() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("external/not/shared/no/auth")
    public ResponseEntity<?> externalNotSharedNoAuth() {
        return ResponseEntity.ok().build();
    }
}
