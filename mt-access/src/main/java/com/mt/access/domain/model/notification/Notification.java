package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import java.util.Collections;
import java.util.Set;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification extends Auditable {
    @Embedded
    private NotificationId notificationId;
    private Long timestamp;
    @Convert(converter = StringSetConverter.class)
    private Set<String> descriptions;
    private String title;

    public Notification(HangingTxDetected deserialize) {
        super();
        id = deserialize.getId();
        notificationId = new NotificationId();
        timestamp = deserialize.getTimestamp();
        title = "HANGING_TX";
        descriptions = Collections.singleton(deserialize.getChangeId());
    }

    public Notification(NewUserRegistered event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "NEW_USER_REGISTER";
        descriptions = Collections.singleton(event.getEmail().getEmail());
    }

    public Notification(ProjectOnboardingComplete event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "NEW_PROJECT_CREATED";
        descriptions = Collections.singleton(event.getProjectName());
    }

    public Notification(ProxyCacheCheckFailedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "PROXY_CHECK_FAILED";
    }

    public Notification(CrossDomainValidationService.ValidationFailedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "VALIDATION_FAILED";
        descriptions = Collections.singleton(event.getMessage());
    }
}
