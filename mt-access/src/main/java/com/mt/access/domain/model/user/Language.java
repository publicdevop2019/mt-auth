package com.mt.access.domain.model.user;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum Language {
    ENGLISH,
    MANDARIN;

    public static class DbConverter extends EnumConverter<Language> {
        public DbConverter() {
            super(Language.class);
        }
    }
}
