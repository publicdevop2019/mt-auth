package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Column;
import lombok.Data;

@Data
public class UserAvatar {
    @Column(name = "avatar_link")
    private String value;

    private UserAvatar() {
    }

    public UserAvatar(String value) {
        this.value = value;
        Validator.isHttpUrl(value);
    }
}
