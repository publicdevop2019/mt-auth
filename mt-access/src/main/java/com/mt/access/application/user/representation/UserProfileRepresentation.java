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
    private String language;
    private String countryCode;
    private String mobileNumber;
    private String avatarLink;
    private String username;


    public UserProfileRepresentation(User user,
                                     @Nullable LoginInfo loginInfo) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        this.createdAt = user.getCreatedAt();
        if (loginInfo != null) {
            lastLoginAt = loginInfo.getLoginAt();
            ipAddress = loginInfo.getIpAddress();
            agent = loginInfo.getAgent();
        }
        if (user.getUserName() != null) {
            this.username = user.getUserName().getValue();
        }
        if (user.getUserAvatar() != null) {
            this.avatarLink = user.getUserAvatar().getValue();
        }
        if (user.getLanguage() != null) {
            this.language = user.getLanguage().name();
        }
        if (user.getMobile() != null) {
            this.countryCode = user.getMobile().getCountryCode();
            this.mobileNumber = user.getMobile().getMobileNumber();
        }
    }
}
