package com.mt.access.domain.model.notification;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

public enum NotificationStatus {
    PENDING,
    DELIVERED;
    public static class DbConverter extends EnumSetConverter<NotificationStatus> {
        public DbConverter() {
            super(NotificationStatus.class);
        }
    }
}
