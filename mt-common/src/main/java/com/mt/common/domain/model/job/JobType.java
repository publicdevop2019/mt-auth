package com.mt.common.domain.model.job;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum JobType {
    CLUSTER,
    SINGLE;
    public static class DbConverter extends EnumConverter<JobType> {
        public DbConverter() {
            super(JobType.class);
        }
    }
}
