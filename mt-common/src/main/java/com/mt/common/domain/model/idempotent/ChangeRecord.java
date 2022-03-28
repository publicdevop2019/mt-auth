package com.mt.common.domain.model.idempotent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"changeId", "entityType"}))
@Data
@NoArgsConstructor
public class ChangeRecord {
    @Id
    private Long id;
    @Column(nullable = false)
    private String changeId;
    @Column(nullable = false)
    private String entityType;
    @Column
    private String returnValue;

    public ChangeRecord(Long id, String changeId, String entityType, String returnValue) {
        this.id = id;
        this.changeId = changeId;
        this.entityType = entityType;
        this.returnValue = returnValue;
    }
}
