package com.mt.access.domain.model.user;

import com.mt.access.domain.model.image.ImageId;
import javax.persistence.Column;
import lombok.Data;

@Data
public class UserAvatar {
    @Column(name = "avatar_link")
    private String value;

    private UserAvatar() {
    }

    public UserAvatar(ImageId imageId) {
        this.value = imageId.getDomainId();
    }
}
