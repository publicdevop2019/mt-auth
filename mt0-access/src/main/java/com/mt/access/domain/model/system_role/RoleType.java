package com.mt.access.domain.model.system_role;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum RoleType {
    CLIENT,
    USER;

    public static class DBConverter extends EnumConverter<RoleType> {
        public DBConverter() {
            super(RoleType.class);
        }
    }
}
