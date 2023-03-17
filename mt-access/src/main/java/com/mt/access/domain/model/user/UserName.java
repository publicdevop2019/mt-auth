package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserName {
    @Column(name = "username")
    private String value;

    private UserName() {
    }

    /**
     * username, must be less than 25 char, cannot be blank and white listed value only.
     *
     * @param value username
     */
    public UserName(String value) {
        Validator.notBlank(value);
        Validator.whitelistOnly(value);
        Validator.lengthLessThanOrEqualTo(value, 25);
        this.value = value;
    }
}
