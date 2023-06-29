package com.mt.test_case.helper.pojo;

import lombok.Data;

@Data
public class ForgetPasswordRequest {

    private String email;

    private String token;

    private String newPassword;
}
