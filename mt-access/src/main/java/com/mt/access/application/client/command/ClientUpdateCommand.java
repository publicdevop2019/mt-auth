package com.mt.access.application.client.command;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClientUpdateCommand extends ClientCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private Integer version;
}
