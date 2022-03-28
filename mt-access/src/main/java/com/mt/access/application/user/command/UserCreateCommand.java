package com.mt.access.application.user.command;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private Long id;

    private String email;

    private String activationCode;

    private String password;
}
