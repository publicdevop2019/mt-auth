package com.mt.access.domain.model.role;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum RoleType {
    USER,
    CLIENT;

    public static class DBConverter extends EnumConverter<RoleType> {
        public DBConverter() {
            super(RoleType.class);
        }
    }
}
