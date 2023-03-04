package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.pending_user.PendingUserCreateCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "pending-users")
public class PendingUserResource {
    @PostMapping
    public ResponseEntity<Void> createForPublic(@RequestBody PendingUserCreateCommand command,
                                                @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                String changeId) {
        ApplicationServiceRegistry.getPendingUserApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }
}
