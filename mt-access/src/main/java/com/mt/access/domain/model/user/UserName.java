package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserName  implements Serializable {
    private String value;

    private UserName() {
    }

    /**
     * create username
     *
     * @param value username
     */
    public UserName(String value) {
        if (Checker.notNull(value)) {
            Validator.validRequiredString(5, 50, value);
            value = value.trim();
        }
        this.value = value;
    }
}
