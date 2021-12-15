package com.hw.helper;

import lombok.Data;

@Data
public class PendingResourceOwner {
    private Long id;

    private String email;

    private String activationCode;

    private String password;

}
