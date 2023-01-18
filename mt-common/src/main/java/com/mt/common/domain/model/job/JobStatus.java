package com.mt.common.domain.model.job;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum JobStatus {
    SUCCESS,
    FAILURE;
    public static class DbConverter extends EnumConverter<JobStatus> {
        public DbConverter() {
            super(JobStatus.class);
        }
    }
}
