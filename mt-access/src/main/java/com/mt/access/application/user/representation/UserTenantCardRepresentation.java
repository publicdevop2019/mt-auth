package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.validate.Utility;
import lombok.Data;

@Data
public class UserTenantCardRepresentation {
    private String id;

    private String username;
    private String mobile;
    private String email;


    public UserTenantCardRepresentation(Object o) {
        User user = (User) o;
        if (Utility.notNull(user.getUserName())) {
            username = user.getUserName().getValue();
        }
        if (Utility.notNull(user.getEmail())) {
            email = user.getEmail().getPartialValue();
        }
        if (Utility.notNull(user.getMobile())) {
            mobile = user.getMobile().getPartialValue();
        }
        id = user.getUserId().getDomainId();
    }

}
