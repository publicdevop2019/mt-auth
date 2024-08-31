package com.mt.access.domain.model.token;

import com.mt.access.application.user.representation.UserTokenRepresentation;
import lombok.Data;

@Data
public class TokenGrantContext {
    private boolean isMFARequired=true;
    private UserTokenRepresentation userInfo;
}
