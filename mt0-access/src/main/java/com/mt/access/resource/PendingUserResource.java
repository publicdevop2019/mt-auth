package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.pending_user.PendingUserCreateCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

@RestController
@RequestMapping(produces = "application/json", path = "pending-users")
public class PendingUserResource {
    @PostMapping
    public ResponseEntity<Void> createForPublic(@RequestBody PendingUserCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getPendingUserApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }
}
