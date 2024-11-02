package com.mt.access.domain.model.user;

import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.common.domain.model.validate.Checker;
import lombok.Data;

@Data
public class LoginResult {
    private Boolean allowed;
    private Boolean mfaRequired;
    private boolean selectRequired = false;
    private Boolean invalidMfa;
    private String partialEmail;
    private String partialMobile;
    private MfaDeliverMethod deliverMethod;

    public static LoginResult allow() {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(true);
        return loginResult;
    }

    public static LoginResult mfaMissing(User user) {
        LoginResult loginResult = mfaMissing();
        if (Checker.notNull(user.getEmail())) {
            loginResult.partialEmail = user.getEmail().getPartialValue();
        } else {
            loginResult.partialMobile = user.getMobile().getPartialValue();
        }
        return loginResult;
    }

    private static LoginResult mfaMissing() {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(false);
        loginResult.setMfaRequired(true);
        return loginResult;
    }

    public static LoginResult askUserSelect(User user) {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(false);
        loginResult.setMfaRequired(true);
        loginResult.setSelectRequired(true);
        loginResult.setPartialEmail(user.getEmail().getPartialValue());
        loginResult.setPartialMobile(user.getMobile().getPartialValue());
        return loginResult;
    }

    public static LoginResult mfaMissMatch() {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(false);
        loginResult.setInvalidMfa(true);
        return loginResult;
    }

    public static LoginResult mfaMissingAfterSelect(
        MfaDeliverMethod deliverMethod,
        User user
    ) {
        LoginResult loginResult = mfaMissing();
        if (MfaDeliverMethod.EMAIL.equals(deliverMethod)) {
            loginResult.partialEmail = user.getEmail().getPartialValue();
        } else {
            loginResult.partialMobile = user.getMobile().getPartialValue();
        }
        return loginResult;
    }
}
