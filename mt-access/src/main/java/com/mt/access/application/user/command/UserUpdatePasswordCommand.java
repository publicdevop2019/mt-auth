package com.mt.access.application.user.command;

import lombok.Data;

@Data
public class UserUpdatePasswordCommand {
    private String currentPwd;
    private String password;
}
