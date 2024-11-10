package com.mt.access.domain.model.token;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;

public enum LoginType {
    MOBILE_W_CODE("mobile_w_code"),
    EMAIL_W_CODE("email_w_code"),
    USERNAME_W_PWD("username_w_pwd"),
    MOBILE_W_PWD("mobile_w_pwd"),
    EMAIL_W_PWD("email_w_pwd");
    public final String label;

    LoginType(String label) {
        this.label = label;
    }

    public static LoginType parse(String type) {
        if (MOBILE_W_CODE.label.equalsIgnoreCase(type)) {
            return LoginType.MOBILE_W_CODE;
        }
        if (EMAIL_W_CODE.label.equalsIgnoreCase(type)) {
            return LoginType.EMAIL_W_CODE;
        }
        if (USERNAME_W_PWD.label.equalsIgnoreCase(type)) {
            return LoginType.USERNAME_W_PWD;
        }
        if (MOBILE_W_PWD.label.equalsIgnoreCase(type)) {
            return LoginType.MOBILE_W_PWD;
        }
        if (EMAIL_W_PWD.label.equalsIgnoreCase(type)) {
            return LoginType.EMAIL_W_PWD;
        }
        throw new DefinedRuntimeException("invalid params", "1089",
            HttpResponseCode.BAD_REQUEST);
    }
}
