package com.mt.access.application.sub_request.command;

import lombok.Data;

@Data
public class CreateSubRequestCommand {
    private String endpointId;
    private String projectId;
    private int maxInvokePerSec;
    private int maxInvokePerMin;
}
