package com.mt.access.application.user.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserForgetPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;
}
