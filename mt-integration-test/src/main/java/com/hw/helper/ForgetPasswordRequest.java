package com.hw.helper;

import lombok.Data;

@Data
public class ForgetPasswordRequest {

    private String email;

    private String token;

    private String newPassword;
}
