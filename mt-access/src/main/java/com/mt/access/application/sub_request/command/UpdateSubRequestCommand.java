package com.mt.access.application.sub_request.command;

import lombok.Data;

@Data
public class UpdateSubRequestCommand {
    private int maxInvokePerSecond;
    private int maxInvokePerMinute;
}
