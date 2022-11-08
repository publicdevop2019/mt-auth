package com.mt.access.domain.model.sub_request;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum SubRequestStatus {
    PENDING,
    APPROVED,
    CANCELLED,
    REJECTED;

    public static class DbConverter extends EnumConverter<SubRequestStatus> {
        public DbConverter() {
            super(SubRequestStatus.class);
        }
    }
}
