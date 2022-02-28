package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collections;
import java.util.Set;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification extends Auditable {
    @Id
    private Long id;
    private NotificationId notificationId;
    private Long timestamp;
    @Convert(converter = StringSetConverter.class)
    private Set<String> descriptions;
    private String title;

    public Notification(HangingTxDetected deserialize) {
        id = deserialize.getId();
        notificationId = new NotificationId();
        timestamp = deserialize.getTimestamp();
        title = "HANGING_TX";
        descriptions = Collections.singleton(deserialize.getChangeId());
    }

    public Notification(NewUserRegistered event) {
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "NEW_USER_REGISTER";
        descriptions = Collections.singleton(event.getEmail().getEmail());
    }

    public Notification(ProjectCreated event) {
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "NEW_PROJECT_CREATED";
        descriptions = Collections.singleton(event.getProjectName());
    }
}
