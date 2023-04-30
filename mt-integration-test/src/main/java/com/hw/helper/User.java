package com.hw.helper;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class User {

    protected String id;

    private String email;

    private String password;

    private Boolean locked;

    private Integer version;
    private String mobileNumber;
    private String countryCode;
}
