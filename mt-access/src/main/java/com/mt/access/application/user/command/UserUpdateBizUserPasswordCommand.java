package com.mt.access.application.user.command;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserUpdateBizUserPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String currentPwd;
    private String password;
}
