package com.mt.access.application.user.command;

import com.mt.access.domain.model.user.Language;
import lombok.Data;

@Data
public class UserUpdateProfileCommand {
    private String countryCode;

    private String mobileNumber;
    private Language language;

    private String username;
}
