package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import lombok.Data;

@Data
public class UserProfileRepresentation {
    private String id;

    private String email;

    private Long createdAt;


    public UserProfileRepresentation(User user) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        this.createdAt = user.getCreatedAt().getTime();
    }
}
