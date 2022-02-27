package com.mt.access.domain.model.ticket;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.user.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
public class TicketInfo {
    public static String USER_ID = "uid";//required
    public static String CLIENT_ID = "clientId";
    public static String AUD = "aud";
    public static String AUTHORITIES = "permissionIds";
    public static String SCOPES = "scope";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketInfo that = (TicketInfo) o;
        return Objects.equals(exp, that.exp) && Objects.equals(userId, that.userId) && Objects.equals(clientId, that.clientId) && Objects.equals(aud, that.aud);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exp, userId, clientId, aud);
    }
}
