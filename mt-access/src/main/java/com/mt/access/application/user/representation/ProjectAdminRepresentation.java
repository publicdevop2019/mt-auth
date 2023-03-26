package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import lombok.Data;

@Data
public class ProjectAdminRepresentation {
    private final String id;
    private final String email;
    private String name;

    public ProjectAdminRepresentation(User user) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        if (user.getUserName() != null) {
            this.name = user.getUserName().getValue();
        }
    }
}
