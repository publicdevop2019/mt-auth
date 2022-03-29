package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class UserEmail {
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
}
