package com.mt.access.messenger.domain.model.system_notification;

import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Getter
@NoArgsConstructor
public class SystemNotification extends Auditable {
    @Id
    private Long id;
    private SystemNotificationId systemNotificationId;
    private Long timestamp;
    private String details;

    public SystemNotification(HangingTxDetected deserialize) {
        id = deserialize.getId();
        systemNotificationId = new SystemNotificationId();
        timestamp = deserialize.getTimestamp();
        details = "Hanging transaction detected with change id " + deserialize.getChangeId();
    }
}
