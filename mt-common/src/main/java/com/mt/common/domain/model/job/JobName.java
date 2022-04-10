package com.mt.common.domain.model.job;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum JobName {
    DATA_VALIDATION,
    PROXY_VALIDATION,
    EVENT_SCAN,
    MISSED_EVENT_SCAN,
    KEEP_WS_CONNECTION;

    public static class DbConverter extends EnumConverter<JobName> {
        public DbConverter() {
            super(JobName.class);
        }
    }
}
