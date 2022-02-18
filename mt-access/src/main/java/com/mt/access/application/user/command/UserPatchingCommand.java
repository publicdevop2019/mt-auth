package com.mt.access.application.user.command;

import com.mt.access.domain.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPatchingCommand {
    private boolean locked;

    public UserPatchingCommand(User bizUser) {
        this.locked = bizUser.isLocked();
    }
}
