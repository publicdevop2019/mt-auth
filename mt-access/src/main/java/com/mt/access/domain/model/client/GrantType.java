package com.mt.access.domain.model.client;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

public enum GrantType {
    CLIENT_CREDENTIALS,
    PASSWORD,
    REFRESH_TOKEN,
    AUTHORIZATION_CODE;

    public static class DbConverter extends EnumSetConverter<GrantType> {
        public DbConverter() {
            super(GrantType.class);
        }
    }

}
