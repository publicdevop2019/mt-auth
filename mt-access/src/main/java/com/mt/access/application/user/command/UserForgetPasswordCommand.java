package com.mt.access.application.user.command;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.i18n.SupportedLocale;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserForgetPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;
    private String countryCode;
    private String mobileNumber;
    private ClientId clientId;
    private SupportedLocale locale;
}
