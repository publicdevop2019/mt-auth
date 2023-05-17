package com.mt.test_case.helper.pojo;

import lombok.Data;

import java.util.List;

@Data
public class User {

    protected String id;

    private String email;

    private String password;

    private Boolean locked;

    private Integer version;
    private String mobileNumber;
    private String countryCode;
    private String username;
    private List<String> roles;
}
