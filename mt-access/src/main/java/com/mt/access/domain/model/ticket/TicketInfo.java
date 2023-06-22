package com.mt.access.domain.model.ticket;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.user.UserId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class TicketInfo {
    /**
     * below is required.
     */
    public static final String USER_ID = "uid";
    public static final String CLIENT_ID = "clientId";
    public static final String AUD = "aud";
    public static final String PERMISSION_IDS = "permissionIds";
    public static final String SCOPES = "scope";
    private Long exp;
    private UserId userId;
    private ClientId clientId;
    private ClientId aud;

    private TicketInfo(UserId userId, ClientId clientId, ClientId aud) {
        this.exp = System.currentTimeMillis() + 5000L;
        this.userId = userId;
        this.clientId = clientId;
        this.aud = aud;
    }

    public static TicketInfo create(UserId userId, ClientId clientId, ClientId aud) {
        return new TicketInfo(userId, clientId, aud);
    }

}
