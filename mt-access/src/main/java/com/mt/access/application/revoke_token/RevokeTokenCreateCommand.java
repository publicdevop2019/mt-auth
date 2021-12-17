package com.mt.access.application.revoke_token;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RevokeTokenCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String id;

    public RevokeTokenCreateCommand(String id) {
        this.id = id;
    }
}
