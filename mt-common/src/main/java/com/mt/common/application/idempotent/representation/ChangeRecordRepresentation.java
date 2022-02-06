package com.mt.common.application.idempotent.representation;

import com.mt.common.domain.model.idempotent.ChangeRecord;
import lombok.Data;

@Data
public class ChangeRecordRepresentation {
    private Long id;

    private String changeId;
    private String entityType;

    public ChangeRecordRepresentation(ChangeRecord changeRecord) {
        this.id = changeRecord.getId();
        this.changeId = changeRecord.getChangeId();
        this.entityType = changeRecord.getEntityType();
    }
}
