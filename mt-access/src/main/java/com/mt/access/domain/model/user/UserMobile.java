package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.Getter;

public class UserMobile implements Serializable {
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
        this.countryCode = countryCode;
        this.mobileNumber = mobileNumber;
        Validator.lengthGreaterThanOrEqualTo(mobileNumber, 10);
        Validator.lengthLessThanOrEqualTo(mobileNumber, 11);
        Validator.lengthGreaterThanOrEqualTo(countryCode, 1);
        Validator.lengthLessThanOrEqualTo(countryCode, 3);
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
        return Objects.equals(countryCode, that.countryCode) &&
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
