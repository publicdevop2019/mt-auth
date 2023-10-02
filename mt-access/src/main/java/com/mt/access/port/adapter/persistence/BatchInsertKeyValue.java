package com.mt.access.port.adapter.persistence;

import lombok.Data;

@Data
public class BatchInsertKeyValue {
    private Long id;
    private String value;

    public BatchInsertKeyValue(Long id, String value) {
        this.id = id;
        this.value = value;
    }
}
