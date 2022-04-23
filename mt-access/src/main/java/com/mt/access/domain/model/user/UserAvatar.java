package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.Validator;
import lombok.Data;

@Data
public class UserAvatar {
    private String avatarLink;

    public UserAvatar(String avatarLink) {
        this.avatarLink = avatarLink;
        Validator.isHttpUrl(avatarLink);
    }
}
