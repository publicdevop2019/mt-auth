package com.mt.access.domain.model.user.event;

public enum MfaDeliverMethod {
    MOBILE,
    EMAIL;

    public static MfaDeliverMethod parse(String mfaMethod) {
        if ("mobile".equalsIgnoreCase(mfaMethod)) {
            return MOBILE;
        }
        if ("email".equalsIgnoreCase(mfaMethod)) {
            return EMAIL;
        }
        return null;
    }
}
