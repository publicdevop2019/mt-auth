package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSystemCardRepresentation {
    private String id;

    private String email;

    public UserSystemCardRepresentation(User o) {
        id = o.getUserId().getDomainId();
        email = o.getEmail().getEmail();
    }
}
