package com.mt.access.domain.model.user;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.Getter;

public class UserMobile implements Serializable {
    private static final Pattern COUNTRY_CODE_REGEX = Pattern.compile("^[0-9]{1,3}$");
    private static final Pattern MOBILE_NUMBER_REGEX = Pattern.compile("^[0-9]{10,11}$");
    @Column
    @NotNull
    @Getter
    String countryCode;
    @Column
    @NotNull
    @Getter
    String mobileNumber;

    private UserMobile() {
    }

    /**
     * user's mobile info, only support mobile number >=10 and <=11.
     *
     * @param countryCode  country code of mobile
     * @param mobileNumber mobile number
     */
    public UserMobile(String countryCode, String mobileNumber) {
        setCountryCode(countryCode);
        setMobileNumber(mobileNumber);
    }

    public void setCountryCode(String countryCode) {
        Validator.notNull(countryCode);
        Matcher matcher = COUNTRY_CODE_REGEX.matcher(countryCode);
        if (!matcher.find()) {
            throw new DefinedRuntimeException("invalid phone number", "1088",
                HttpResponseCode.BAD_REQUEST);
        }
        this.countryCode = countryCode;
    }

    public void setMobileNumber(String mobileNumber) {
        Validator.notNull(mobileNumber);
        Matcher matcher = MOBILE_NUMBER_REGEX.matcher(mobileNumber);
        if (!matcher.find()) {
            throw new DefinedRuntimeException("invalid phone number", "1088",
                HttpResponseCode.BAD_REQUEST);
        }
        this.mobileNumber = mobileNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserMobile that = (UserMobile) o;
        return Objects.equals(countryCode, that.countryCode)
            &&
            Objects.equals(mobileNumber, that.mobileNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, mobileNumber);
    }

    public String value() {
        return countryCode + " " + mobileNumber;
    }
}
