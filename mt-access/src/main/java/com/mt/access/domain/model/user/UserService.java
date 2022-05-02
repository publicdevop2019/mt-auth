package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.PatchCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void updatePassword(User user, CurrentPassword currentPwd, UserPassword password) {
        if (!DomainRegistry.getEncryptionService().compare(user.getPassword(), currentPwd)) {
            throw new IllegalArgumentException("wrong password");
        }
        user.setPassword(password);
        DomainRegistry.getUserRepository().add(user);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void forgetPassword(UserEmail email) {
        Optional<User> user = DomainRegistry.getUserRepository().searchExistingUserWith(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user does not exist");
        }
        PasswordResetCode passwordResetToken = new PasswordResetCode();
        user.get().setPwdResetToken(passwordResetToken);
        DomainRegistry.getUserRepository().add(user.get());

    }

    public void resetPassword(UserEmail email, UserPassword newPassword, PasswordResetCode token) {
        Optional<User> user = DomainRegistry.getUserRepository().searchExistingUserWith(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user does not exist");
        }
        if (user.get().getPwdResetToken() == null) {
            throw new IllegalArgumentException("token not exist");
        }
        if (!user.get().getPwdResetToken().equals(token)) {
            throw new IllegalArgumentException("token mismatch");
        }
        user.get().setPassword(newPassword);
        DomainRegistry.getUserRepository().add(user.get());
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPasswordChanged(user.get().getUserId()));
    }

    public void batchLock(List<PatchCommand> commands) {
        if (Boolean.TRUE.equals(commands.get(0).getValue())) {
            commands.stream().map(e -> new UserId(e.getPath().split("/")[1])).forEach(e -> {
                CommonDomainRegistry.getDomainEventRepository().append(new UserGetLocked(e));
            });
        }
        DomainRegistry.getUserRepository().batchLock(commands);
    }

    public void updateLastLogin(UserLoginRequest command) {
        UserId userId = command.getUserId();
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().ofId(userId);
        loginInfo.ifPresentOrElse(e -> e.updateLastLogin(command), () -> {
            LoginInfo loginInfo1 = new LoginInfo(command);
            DomainRegistry.getLoginInfoRepository().add(loginInfo1);
        });
        LoginHistory loginHistory = new LoginHistory(command);
        DomainRegistry.getLoginHistoryRepository().add(loginHistory);
    }

    public boolean isMFARequired(UserId userId, UserSession userSession) {
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
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        User user1 = user.get();
        MfaInfo mfaInfo = user1.getMfaInfo();
        if (mfaInfo == null) {
            return false;
        }
        return mfaInfo.validate(mfaCode, mfaId);
    }

    private <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> mostFrequentIp(Map<K, V> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue());
    }

    public MfaId triggerMfa(UserId userId) {
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        User user1 = user.get();
        MfaInfo mfaInfo = MfaInfo.create();
        user1.setMfaInfo(mfaInfo);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserMfaNotificationEvent(user1, mfaInfo));
        return mfaInfo.getId();
    }
}
