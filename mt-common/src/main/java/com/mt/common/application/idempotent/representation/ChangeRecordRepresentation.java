package com.mt.common.application.idempotent.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.common.domain.model.idempotent.ChangeRecord;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Data
public class ChangeRecordRepresentation {
    private Long id;

    private String changeId;
    private String entityType;

    private Set<String> deletedIds;
    private String query;
    private Object replacedVersion;
    private Object requestBody;
    @Autowired
    private ObjectMapper om;

    public ChangeRecordRepresentation(ChangeRecord changeRecord) {
        this.id = changeRecord.getId();
        this.changeId = changeRecord.getChangeId();
        this.entityType = changeRecord.getEntityType();
    }
}
