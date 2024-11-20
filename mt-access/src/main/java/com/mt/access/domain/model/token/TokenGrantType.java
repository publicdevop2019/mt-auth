package com.mt.access.domain.model.token;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;

/**
 * same as Client's Grant Type but with logic specific during token grant
 */
public enum TokenGrantType {
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token"),
    AUTHORIZATION_CODE("authorization_code");
    public final String label;

    TokenGrantType(String label) {
        this.label = label;
    }

    public static TokenGrantType parse(String type) {
        if (CLIENT_CREDENTIALS.label.equalsIgnoreCase(type)) {
            return TokenGrantType.CLIENT_CREDENTIALS;
        }
        if (PASSWORD.label.equalsIgnoreCase(type)) {
            return TokenGrantType.PASSWORD;
        }
        if (REFRESH_TOKEN.label.equalsIgnoreCase(type)) {
            return TokenGrantType.REFRESH_TOKEN;
        }
        if (AUTHORIZATION_CODE.label.equalsIgnoreCase(type)) {
            return TokenGrantType.AUTHORIZATION_CODE;
        }
        throw new DefinedRuntimeException("invalid params", "1089",
            HttpResponseCode.BAD_REQUEST);
    }
}
