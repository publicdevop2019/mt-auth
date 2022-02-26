package com.mt.access.messenger.resource;

import com.mt.messenger.application.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class EmailDeliveryResource {

    @PostMapping("notifyBy/email/newOrder")
    public ResponseEntity<?> sendOrderInfoToAccount() {
        ApplicationServiceRegistry.getEmailDeliverApplicationService().sendNewOrderEmail();
        return ResponseEntity.ok().build();
    }
}


