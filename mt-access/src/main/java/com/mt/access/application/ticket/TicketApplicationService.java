package com.mt.access.application.ticket;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.ticket.SignedTicket;
import com.mt.access.domain.model.user.UserId;
import org.springframework.stereotype.Service;

@Service
public class TicketApplicationService {
    public SignedTicket create(String rawClientId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        ClientId clientId = DomainRegistry.getCurrentUserService().getClientId();
        return DomainRegistry.getTicketService()
            .create(userId, clientId, new ClientId(rawClientId));
    }
}
