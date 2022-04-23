package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Column;
import lombok.Data;

@Data
public class UserName {
    private String username;

    /**
     * username, must be less then 25 char, cannot be blank and white listed value only.
     *
     * @param username username
     */
    public UserName(String username) {
        Validator.notBlank(username);
        Validator.whitelistOnly(username);
        Validator.lengthLessThanOrEqualTo(username, 25);
        this.username = username;
    }
}
