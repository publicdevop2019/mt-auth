package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.validate.Checker;
import lombok.Data;

@Data
public class UserTenantCardRepresentation {
    private String id;

    private String username;
    private String mobile;
    private String email;


    public UserTenantCardRepresentation(Object o) {
        User user = (User) o;
        if (Checker.notNull(user.getUserName())) {
            username = user.getUserName().getValue();
        }
        if (Checker.notNull(user.getEmail())) {
            email = user.getEmail().getPartialValue();
        }
        if (Checker.notNull(user.getMobile())) {
            mobile = user.getMobile().getPartialValue();
        }
        id = user.getUserId().getDomainId();
    }

}
