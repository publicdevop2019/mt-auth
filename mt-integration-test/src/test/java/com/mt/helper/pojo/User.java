package com.mt.helper.pojo;

import java.util.List;
import lombok.Data;

@Data
public class User {

    protected String id;

    private String email;

    private String password;

    private Boolean locked;

    private Integer version;
    private String mobileNumber;
    private Object language;
    private String countryCode;
    private String username;
    private List<String> roles;
}
