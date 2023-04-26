package com.hw.helper;

import lombok.Data;

@Data
public class PendingUser {
    private Long id;

    private String email;

    private String activationCode;

    private String password;
    private String mobileNumber;
    private String countryCode;
}
