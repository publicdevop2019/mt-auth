package com.mt.access.application.user.command;

import lombok.Data;

@Data
public class UserAddMobileCommand {
    private String countryCode;
    private String mobileNumber;
}
