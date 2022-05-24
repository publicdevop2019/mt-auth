package com.mt.access.domain.model.notification;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

public enum NotificationType {
    EMAIL,
    BELL,
    SMS;

    public static class DbConverter extends EnumSetConverter<NotificationType> {
        public DbConverter() {
            super(NotificationType.class);
        }
    }
}
