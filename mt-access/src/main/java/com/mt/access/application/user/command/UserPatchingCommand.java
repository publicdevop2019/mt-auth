package com.mt.access.application.user.command;

import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class UserPatchingCommand {
    private boolean locked;
    private Set<SystemRoleId> grantedAuthorities;

    public UserPatchingCommand(User bizUser) {
        this.locked = bizUser.isLocked();
        this.grantedAuthorities = bizUser.getRoles();
    }
}
