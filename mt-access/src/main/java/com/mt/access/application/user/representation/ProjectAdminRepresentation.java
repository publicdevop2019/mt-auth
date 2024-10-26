package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.validate.Checker;
import lombok.Data;

@Data
public class ProjectAdminRepresentation {
    private final String id;
    private String email;
    private String mobile;
    private String name;

    public ProjectAdminRepresentation(User user) {
        this.id = user.getUserId().getDomainId();
        if (Checker.notNull(user.getEmail())) {
            this.email = user.getEmail().getPartialValue();
        }
        if (Checker.notNull(user.getMobile())) {
            this.mobile = user.getMobile().getPartialValue();
        }
        if (Checker.notNull(user.getUserName())) {
            this.name = user.getUserName().getValue();
        }
    }
}
