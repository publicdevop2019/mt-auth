package com.mt.access.domain.model.verification_code;

import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import java.util.regex.Matcher;
import lombok.Getter;

public class RegistrationMobile extends DomainId {
    @Getter
    private String countryCode;
    @Getter
    private String mobileNumber;

    private RegistrationMobile() {
    }

    /**
     * user's mobile info, only support mobile number >=10 and <=11.
     *
     * @param countryCode  country code of mobile
     * @param mobileNumber mobile number
     */
    public RegistrationMobile(String countryCode, String mobileNumber) {
        setCountryCode(countryCode);
        setMobileNumber(mobileNumber);
        setDomainId(value());
    }

    private void setCountryCode(String countryCode) {
        Validator.notNull(countryCode);
        Matcher matcher = AppConstant.COUNTRY_CODE_REGEX.matcher(countryCode);
        if (!matcher.find()) {
            throw new DefinedRuntimeException("invalid phone number", "1088",
                HttpResponseCode.BAD_REQUEST);
        }
        this.countryCode = countryCode;
    }

    private void setMobileNumber(String mobileNumber) {
        Validator.notNull(mobileNumber);
        Matcher matcher = AppConstant.MOBILE_NUMBER_REGEX.matcher(mobileNumber);
        if (!matcher.find()) {
            throw new DefinedRuntimeException("invalid phone number", "1088",
                HttpResponseCode.BAD_REQUEST);
        }
        this.mobileNumber = mobileNumber;
    }

    private String value() {
        return "+" + countryCode + " " + mobileNumber;
    }
}
