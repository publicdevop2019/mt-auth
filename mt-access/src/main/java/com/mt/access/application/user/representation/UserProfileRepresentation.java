package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.User;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class UserProfileRepresentation {
    private String id;

    private String email;

    private Long createdAt;
    private Long lastLoginAt;
    private String ipAddress;
    private String agent;


    public UserProfileRepresentation(User user,
                                     @Nullable LoginInfo loginInfo) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        this.createdAt = user.getCreatedAt().getTime();
        if (loginInfo != null) {
            lastLoginAt = loginInfo.getLoginAt().getTime();
            ipAddress = loginInfo.getIpAddress();
            agent = loginInfo.getAgent();
        }
    }
}
