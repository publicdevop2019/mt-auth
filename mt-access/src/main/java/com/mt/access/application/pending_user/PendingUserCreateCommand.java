package com.mt.access.application.pending_user;

import java.io.Serializable;
import lombok.Data;

@Data
public class PendingUserCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;

}
