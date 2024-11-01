package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.verification_code.VerificationCodeCreateCommand;
import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.jwt.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "verification-code")
public class VerificationCodeResource {
    @PostMapping
    public ResponseEntity<Void> sendCode(
        @RequestBody VerificationCodeCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        ClientId clientId = new ClientId(JwtUtility.getClientId(jwt));
        command.setClientId(clientId);
        ApplicationServiceRegistry.getVerificationCodeApplicationService()
            .sendCode(command, changeId);
        return ResponseEntity.ok().build();
    }
}
