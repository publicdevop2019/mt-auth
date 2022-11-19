package com.mt.access.application.endpoint.command;

import lombok.Data;

@Data
public class EndpointExpireCommand {
    private String expireReason;
}
