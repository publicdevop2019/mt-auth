package com.mt.access.domain.model.user;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserName implements Serializable {
    private String value;

    private UserName() {
    }

    /**
     * create username
     *
     * @param value username
     */
    public UserName(String value) {
        Validator.notNull(value);
        Validator.validRequiredString(5, 50, value);
        //make sure it's not email or mobile
        if (value.contains("@") || value.matches("\\d+")) {
            throw new DefinedRuntimeException("invalid username", "1002",
                HttpResponseCode.BAD_REQUEST);
        }
        value = value.trim();
        this.value = value;
    }
}
