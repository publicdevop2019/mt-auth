package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import lombok.Data;

@Data
public class UserPublicCardRepresentation {
    private String id;

    public UserPublicCardRepresentation(User bizUser) {
        this.id = bizUser.getUserId().getDomainId();
    }
}
