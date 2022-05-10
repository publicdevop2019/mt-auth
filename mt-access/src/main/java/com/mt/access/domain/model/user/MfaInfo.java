package com.mt.access.domain.model.user;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import lombok.Getter;

@Embeddable
public class MfaInfo {
    @Embedded
    @Getter
    private MfaCode code;
    @Getter
    @Embedded
    private MfaId id;

    public static MfaInfo create() {
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.code = new MfaCode();
        mfaInfo.id = new MfaId();
        return mfaInfo;
    }

    public boolean validate(String mfaCode, String mfaId) {
        if (code == null || id == null) {
            return false;
        }
        return mfaCode.equals(code.getValue()) && mfaId.equals(id.getValue());
    }
}
