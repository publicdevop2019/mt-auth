package com.mt.common.application.idempotent;

import lombok.Data;

@Data
public class CreateChangeRecordCommand {

    private String changeId;
    private String aggregateName;
    private String returnValue;
    private boolean emptyOpt =false;
}
