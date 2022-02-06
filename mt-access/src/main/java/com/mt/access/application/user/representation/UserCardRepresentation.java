package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserCardRepresentation {
    private String id;

    private String email;

    private boolean locked;
    private long createdAt;

    public UserCardRepresentation(Object o) {
        User user = (User) o;
        email = user.getEmail().getEmail();
        id = user.getUserId().getDomainId();
        locked = user.isLocked();
        this.createdAt = user.getCreatedAt().getTime();
    }

}
