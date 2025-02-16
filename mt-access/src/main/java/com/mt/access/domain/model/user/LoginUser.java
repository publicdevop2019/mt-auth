package com.mt.access.domain.model.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginUser {
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Setter(AccessLevel.PRIVATE)
    private Boolean locked;
    @Setter(AccessLevel.PRIVATE)
    private UserPassword password;

    private LoginUser() {
    }

    public static LoginUser deserialize(UserId userId, Boolean locked, UserPassword password) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setPassword(password);
        loginUser.setLocked(locked);
        return loginUser;
    }
}
