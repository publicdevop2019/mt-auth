package com.mt.access.application.pending_user;

import com.mt.access.domain.model.client.ClientId;
import lombok.Data;

@Data
public class PendingUserCreateCommand {
    private String email;
    private ClientId clientId;
}
