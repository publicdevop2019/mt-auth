package com.mt.access.application.user.command;

import java.io.Serializable;
import lombok.Data;

@Data
public class UpdateUserCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private Boolean locked;
}
