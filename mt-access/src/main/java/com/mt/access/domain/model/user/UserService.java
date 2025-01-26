package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void updatePassword(User user, @Nullable CurrentPassword currentPwd,
                               UserPassword password,
                               TransactionContext context) {
        if (Utility.notNull(user.getPassword())) {
            if (!DomainRegistry.getEncryptionService()
                .compare(Utility.notNull(currentPwd) ? currentPwd.getRawPassword() : null,
                    user.getPassword().getPassword())) {
                throw new DefinedRuntimeException("wrong password", "1000",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        User updated = user.updatePassword(password);
        DomainRegistry.getUserRepository().update(user, updated);
        context
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void updateLastLogin(UserLoginRequest command, ProjectId loginProjectId) {
        UserId userId = command.getUserId();
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().query(userId);
        loginInfo.ifPresentOrElse(
            e -> DomainRegistry.getLoginInfoRepository().updateLastLogin(command, userId),
            () -> {
                LoginInfo info = new LoginInfo(command);
                DomainRegistry.getLoginInfoRepository().add(info);
            });
        LoginHistory loginHistory = new LoginHistory(command, loginProjectId);
        DomainRegistry.getLoginHistoryRepository().add(loginHistory);
    }

}
