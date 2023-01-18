package com.mt.common.domain.model.job;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum JobStrategy {
    ASYNC,
    SYNC;

    public static class DbConverter extends EnumConverter<JobStrategy> {
        public DbConverter() {
            super(JobStrategy.class);
        }
    }
}
