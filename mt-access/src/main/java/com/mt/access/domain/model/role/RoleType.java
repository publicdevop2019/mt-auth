package com.mt.access.domain.model.role;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum RoleType {
    USER,
    PROJECT,
    CLIENT_ROOT,
    CLIENT;

    public static class DbConverter extends EnumConverter<RoleType> {
        public DbConverter() {
            super(RoleType.class);
        }
    }
}
