package com.mt.common.domain.model.idempotent;

import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"changeId", "entityType"}))
@Data
public class ChangeRecord {
    public static final String BACKWARD_SUFFIX = "_cancel";
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Column(nullable = false)
    private String changeId;
    @Column(nullable = false)
    private String entityType;
    @Column
    private String returnValue;
    @Column
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
