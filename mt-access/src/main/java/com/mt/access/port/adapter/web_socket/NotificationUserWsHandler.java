package com.mt.access.port.adapter.web_socket;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.jwt.JwtUtility;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jodd.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class NotificationUserWsHandler extends AbstractNotificationWsHandler {
    private final Map<UserId, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserId userId = extractUserId(session);
        userSessionMap.put(userId, session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
        throws Exception {
        UserId userId = extractUserId(session);
        userSessionMap.remove(userId);
        super.afterConnectionClosed(session, status);
    }

    public void notifyUser(UserId userId, String message) {
        log.trace("send notification to user {}", userId);
        WebSocketSession socketSession = userSessionMap.get(userId);
        if (socketSession == null) {
            log.trace("user session not found, ignore operation");
            return;
        }
        try {
            socketSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("error occurred", e);
        }
    }

    private UserId extractUserId(WebSocketSession session) {
        String rawQuery = session.getUri().getRawQuery().replace("jwt=", "");
        String decoded = Base64.decodeToString(rawQuery);
        String rawUserId = JwtUtility.getUserId(decoded);
        return new UserId(rawUserId);
    }
}

