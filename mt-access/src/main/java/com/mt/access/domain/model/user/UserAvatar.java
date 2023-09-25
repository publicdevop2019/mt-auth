package com.mt.access.domain.model.user;

import com.mt.access.domain.model.image.ImageId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
public class UserAvatar  implements Serializable {
    private String value;

    private UserAvatar() {
    }

    public UserAvatar(ImageId imageId) {
        this.value = imageId.getDomainId();
    }
}
