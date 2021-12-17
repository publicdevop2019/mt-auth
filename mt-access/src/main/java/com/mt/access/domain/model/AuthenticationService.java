package com.mt.access.domain.model;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.user.UserId;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface AuthenticationService {
    boolean userInRole(SystemRoleId role);

    Set<String> userRoleIds();

    Set<SystemRoleId> clientScopes();

    boolean isClient();

    boolean isUser();

    Authentication getAuthentication();

    UserId getUserId();

    ClientId getClientId();
}
