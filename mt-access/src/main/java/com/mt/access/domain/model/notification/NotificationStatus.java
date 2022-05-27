package com.mt.access.domain.model.notification;

import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum NotificationStatus {
    PENDING,
    DELIVERED;

    public static class DbConverter extends EnumConverter<NotificationStatus> {
        public DbConverter() {
            super(NotificationStatus.class);
        }
    }
}
