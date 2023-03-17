package com.mt.access.domain.model.user;

import com.mt.access.domain.model.image.ImageId;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserAvatar {
    @Column(name = "avatar_link")
    private String value;

    private UserAvatar() {
    }

    public UserAvatar(ImageId imageId) {
        this.value = imageId.getDomainId();
    }
}
