package com.mt.access.application.client.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientUpdateCommand extends ClientCreateCommand implements Serializable{
    private static final long serialVersionUID = 1;
    private Integer version;
}
