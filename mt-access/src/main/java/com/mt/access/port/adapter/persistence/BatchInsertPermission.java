package com.mt.access.port.adapter.persistence;

import lombok.Data;

@Data
public class BatchInsertPermission {
    private Long id;
    private String domainId;

    public BatchInsertPermission(Long id, String permissionId) {
        this.id = id;
        this.domainId = permissionId;
    }
}
