package com.mt.access.application.user.command;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserResetPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;
    private String countryCode;
    private String mobileNumber;

    private String token;

    private String newPassword;
}
