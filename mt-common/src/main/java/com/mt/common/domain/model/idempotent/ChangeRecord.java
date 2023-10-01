package com.mt.common.domain.model.idempotent;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class ChangeRecord {
    public static final String BACKWARD_SUFFIX = "_cancel";
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    private String changeId;
    private String entityType;
    private String returnValue;
    private Boolean emptyOpt;

    private ChangeRecord() {
    }

    public static ChangeRecord create(String changeId, String entityType) {
        ChangeRecord changeRecord = new ChangeRecord();
        changeRecord.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        changeRecord.setChangeId(changeId);
        changeRecord.setEntityType(entityType);
        changeRecord.setEmptyOpt(Boolean.FALSE);
        return changeRecord;
    }

    public static boolean isBackwardChange(String changeId) {
        return changeId.contains("_cancel");
    }

    public static String getForwardChangeId(String changeId) {
        return changeId.replace(BACKWARD_SUFFIX, "");
    }

    public static ChangeRecord fromDatabaseRow(Long id, String changeId, String entityType,
                                               Boolean emptyOpt, String returnValue) {
        ChangeRecord changeRecord = new ChangeRecord();
        changeRecord.setId(id);
        changeRecord.setChangeId(changeId);
        changeRecord.setEntityType(entityType);
        changeRecord.setEmptyOpt(emptyOpt);
        changeRecord.setReturnValue(returnValue);
        return changeRecord;
    }
}
