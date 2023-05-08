package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.common.domain.CommonDomainRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MfaService {
    public boolean isMfaRequired(UserId userId, UserSession userSession) {
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
        Optional<User> user = DomainRegistry.getUserRepository().by(userId);
        User user1 = user.get();
        MfaInfo mfaInfo = user1.getMfaInfo();
        if (mfaInfo == null) {
            return false;
        }
        return mfaInfo.validate(mfaCode, mfaId);
    }

    public MfaId triggerMfa(UserId userId) {
        Optional<User> user = DomainRegistry.getUserRepository().by(userId);
        User user1 = user.get();
        MfaInfo mfaInfo = MfaInfo.create();
        user1.setMfaInfo(mfaInfo);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserMfaNotificationEvent(user1, mfaInfo));
        return mfaInfo.getId();
    }

    private <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> mostFrequentIp(Map<K, V> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue());
    }
}
