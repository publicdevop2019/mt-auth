package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.LoginUser;
import com.mt.access.domain.model.user.UserPassword;
import lombok.Data;

@Data
public class UserSpringRepresentation {
    private String id;
    private String password;
    private UserPassword userPassword;
    private Boolean locked;

    public UserSpringRepresentation(LoginUser user) {
        id = user.getUserId().getDomainId();
        password = user.getPassword().getPassword();
        userPassword = user.getPassword();
        locked = user.getLocked();
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return id;
    }

    public boolean isAccountNonLocked() {
        return !locked;
    }

}
