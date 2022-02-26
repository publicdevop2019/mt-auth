package com.mt.access.messenger.domain.model.mall_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.event.MallNotificationEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table
@Getter
@NoArgsConstructor
public class MallNotification extends Auditable {
    @Id
    private Long id;
    private MallNotificationId mallNotificationId;
    private Long timestamp;
    @Lob
    private String details;
    private String orderId;
    private String changeId;
    private String name;

    public MallNotification(MallNotificationEvent event) {
        id = event.getId();
        mallNotificationId = new MallNotificationId();
        timestamp = event.getTimestamp();
        details = CommonDomainRegistry.getCustomObjectSerializer().serialize(event.getDetails());
        orderId=event.getDomainId().getDomainId();
        changeId=event.getChangeId();
        name=event.getName();
    }

}
