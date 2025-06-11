package com.mt.common.resource;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.instance.InstanceCreateCommand;
import com.mt.common.application.instance.InstanceRemoveCommand;
import com.mt.common.application.instance.InstanceRenewCommand;
import com.mt.common.application.instance.InstanceRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class InstanceResource {
    @PostMapping("mgmt/instance")
    public ResponseEntity<InstanceRepresentation> create(
        @RequestBody InstanceCreateCommand command) {
        return ResponseEntity
            .ok(CommonApplicationServiceRegistry.getInstanceApplicationService().create(command));
    }

    @DeleteMapping("mgmt/instance")
    public ResponseEntity<Void> remove(
        @RequestBody InstanceRemoveCommand command) {
        CommonApplicationServiceRegistry.getInstanceApplicationService().remove(command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("mgmt/instance")
    public ResponseEntity<Void> renew(
        @RequestBody InstanceRenewCommand command) {
        CommonApplicationServiceRegistry.getInstanceApplicationService().renew(command);
        return ResponseEntity.ok().build();
    }
}
