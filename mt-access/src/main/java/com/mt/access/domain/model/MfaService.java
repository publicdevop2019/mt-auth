package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.MfaCode;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserSession;
import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.access.domain.model.user.event.UserMfaNotification;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MfaService {
    public boolean isMfaRequired(UserId userId, UserSession userSession) {
        log.debug("searching login info");
        Set<LoginHistory> loginHistorySet =
            DomainRegistry.getLoginHistoryRepository().getLast100Login(userId);
        Map<String, Integer> ipCount = new HashMap<>();
        loginHistorySet.forEach(e -> {
            String ipAddress = e.getIpAddress();
            Integer integer = ipCount.get(ipAddress);
            if (integer != null) {
                integer = integer + 1;
                ipCount.put(ipAddress, integer);
            } else {
                ipCount.put(ipAddress, 0);
            }
        });
        Optional<Map.Entry<String, Integer>> stringIntegerEntry = mostFrequentIp(ipCount);
        if (stringIntegerEntry.isEmpty()) {
            return true;
        } else {
            String key = stringIntegerEntry.get().getKey();
            return !key.equals(userSession.getIpAddress());
        }
    }

    public boolean validateMfa(UserId userId, String mfaCode) {
        return DomainRegistry.getTemporaryCodeService()
            .checkCode(mfaCode, MfaCode.EXPIRE_AFTER_MILLI, MfaCode.OPERATION_TYPE,
                userId.getDomainId());
    }

    public void triggerDefaultMfa(ClientId clientId, User user, TransactionContext context) {
        MfaCode mfaCode = new MfaCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(clientId, mfaCode.getValue(), MfaCode.OPERATION_TYPE,
                user.getUserId().getDomainId());
        context
            .append(new UserMfaNotification(user, mfaCode));
    }

    public void triggerSelectedMfa(
        ClientId clientId,
        User user,
        TransactionContext context,
        MfaDeliverMethod deliverMethod
    ) {
        MfaCode mfaCode = new MfaCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(clientId, mfaCode.getValue(), MfaCode.OPERATION_TYPE,
                user.getUserId().getDomainId());

        context
            .append(new UserMfaNotification(user, mfaCode, deliverMethod));
    }

    private <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> mostFrequentIp(Map<K, V> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue());
    }

}
