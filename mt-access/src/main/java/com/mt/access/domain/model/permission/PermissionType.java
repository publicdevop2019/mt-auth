package com.mt.access.domain.model.permission;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum PermissionType {
    COMMON,
    PROJECT,
    API,
    API_ROOT;

    public static class DBConverter extends EnumConverter<PermissionType> {
        public DBConverter() {
            super(PermissionType.class);
        }
    }
}
