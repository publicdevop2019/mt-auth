package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Validator;
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

    public boolean validateMfa(UserId userId, String mfaCode,
                               String mfaId) {
        Validator.notNull(mfaId);
        MfaInfo mfaInfo = DomainRegistry.getUserRepository().getUserMfaInfo(userId);
        if (mfaInfo == null) {
            log.debug("mfa info not found");
            return false;
        }
        log.debug("comparing mfa");
        return mfaInfo.validate(mfaCode, mfaId);
    }

    public MfaId triggerDefaultMfa(User user, TransactionContext context) {
        MfaInfo mfaInfo = MfaInfo.create();
        DomainRegistry.getUserRepository().updateMfaInfo(mfaInfo, user.getUserId());
        context
            .append(new UserMfaNotificationEvent(user, mfaInfo));
        return mfaInfo.getId();
    }

    public MfaId triggerSelectedMfa(User user, TransactionContext context,
                                    MfaDeliverMethod deliverMethod) {
        MfaInfo mfaInfo = MfaInfo.create();
        DomainRegistry.getUserRepository().updateMfaInfo(mfaInfo, user.getUserId());
        context
            .append(new UserMfaNotificationEvent(user, mfaInfo, deliverMethod));
        return mfaInfo.getId();
    }

    private <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> mostFrequentIp(Map<K, V> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue());
    }

}
