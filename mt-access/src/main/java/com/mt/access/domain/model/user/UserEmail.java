package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserEmail implements Serializable {
    private static final String DELIMITER = "@";
    @Getter
    private String email;

    public UserEmail(String email) {
        setEmail(email);
    }

    private void setEmail(String email) {
        this.email = email;
        Validator.notNull(email);
        Validator.notBlank(email);
        Validator.isEmail(email);
    }

    public String getPartialValue() {
        String[] split = email.split(DELIMITER);
        String s = split[0];
        return s.charAt(0) + "***" + DELIMITER + split[1];
    }
}
