package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserAdminRepresentation {
    private String id;

    private String email;
    private boolean locked;
    private Set<String> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private boolean subscription;

    public UserAdminRepresentation(User user) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        this.locked = user.isLocked();
        this.createdBy = user.getCreatedBy();
        this.createdAt = user.getCreatedAt().getTime();
        this.modifiedBy = user.getModifiedBy();
        this.modifiedAt = user.getModifiedAt().getTime();
    }
}
