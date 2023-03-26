package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class ValidationResource {
    /**
     * reset validation status, this should be called after data issue fixed.
     *
     * @return void
     */
    @PostMapping(path = "mgmt/job/validation/reset")
    public ResponseEntity<Void> reset() {
        ApplicationServiceRegistry.getCrossDomainValidationApplicationService().reset();
        return ResponseEntity.ok().build();
    }

}
