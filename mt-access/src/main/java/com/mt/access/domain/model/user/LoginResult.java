package com.mt.access.domain.model.user;

import lombok.Data;

@Data
public class LoginResult {
    private boolean allowed;
    private boolean mfaRequired;
    private boolean invalidMfa;
    private MfaId mfaId;

    public static LoginResult allow() {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(true);
        return loginResult;
    }

    public static LoginResult mfaMissing(MfaId id) {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(false);
        loginResult.setMfaRequired(true);
        loginResult.setMfaId(id);
        return loginResult;
    }

    public static LoginResult mfaMissMatch() {
        LoginResult loginResult = new LoginResult();
        loginResult.setAllowed(false);
        loginResult.setInvalidMfa(true);
        return loginResult;
    }
}
