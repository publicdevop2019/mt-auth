package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.user.PwdResetCode;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import org.springframework.stereotype.Service;

@Service
public class PwdResetService {
    public void forgetPwd(ClientId clientId, UserEmail email, TransactionContext context) {
        UserId userId = DomainRegistry.getUserRepository().getUserId(email);
        DomainRegistry.getCoolDownService()
            .hasCoolDown(userId.getDomainId(), OperationType.PWD_RESET);
        PwdResetCode code = new PwdResetCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(
                clientId,
                code.getValue(),
                PwdResetCode.OPERATION_TYPE,
                email.getEmail()
            );
        context
            .append(new UserPwdResetCodeUpdated(userId, email, code));
    }

    public void forgetPwd(ClientId clientId, UserMobile mobile, TransactionContext context) {
        UserId userId = DomainRegistry.getUserRepository().getUserId(mobile);
        DomainRegistry.getCoolDownService()
            .hasCoolDown(userId.getDomainId(), OperationType.PWD_RESET);
        PwdResetCode code = new PwdResetCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(
                clientId,
                code.getValue(),
                PwdResetCode.OPERATION_TYPE,
                mobile.getValue()
            );
        context
            .append(new UserPwdResetCodeUpdated(userId, mobile, code));
    }

    public void resetPassword(UserEmail email, UserPassword newPassword, PwdResetCode code,
                              TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(email);
        DomainRegistry.getTemporaryCodeService()
            .verifyCode(
                code.getValue(),
                PwdResetCode.EXPIRE_AFTER_MILLI,
                PwdResetCode.OPERATION_TYPE,
                email.getEmail()
            );
        updatePwd(newPassword, context, user);
    }

    public void resetPassword(UserMobile mobile, UserPassword newPassword, PwdResetCode code,
                              TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(mobile);
        DomainRegistry.getTemporaryCodeService()
            .verifyCode(
                code.getValue(),
                PwdResetCode.EXPIRE_AFTER_MILLI,
                PwdResetCode.OPERATION_TYPE,
                mobile.getValue()
            );
        updatePwd(newPassword, context, user);
    }

    private void updatePwd(UserPassword newPassword,
                           TransactionContext context, User user) {

        User after = user.updatePassword(newPassword);
        DomainRegistry.getUserRepository().update(user, after);
        context
            .append(new UserPasswordChanged(user.getUserId()));
    }
}
