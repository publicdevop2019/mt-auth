package com.mt.access.messenger.domain.model.email_delivery.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPwdResetCodeUpdated {
    private String email;
    private String code;
}
