package com.mt.access.application.revoke_token;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RevokeTokenCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String id;

    public RevokeTokenCreateCommand(String id) {
        this.id = id;
    }
}
