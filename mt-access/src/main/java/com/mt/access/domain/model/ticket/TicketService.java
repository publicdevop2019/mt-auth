package com.mt.access.domain.model.ticket;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.user.UserId;

public interface TicketService {
    SignedTicket create(UserId userId, ClientId clientId,ClientId aud);
}
