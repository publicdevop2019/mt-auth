package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
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
    private final boolean ack = false;
    @Embedded
    private NotificationId notificationId;
    private Long timestamp;
    @Convert(converter = StringSetConverter.class)
    private Set<String> descriptions;
    @Convert(converter = NotificationType.DbConverter.class)
    private NotificationType type;
    @Convert(converter = NotificationStatus.DbConverter.class)
    private NotificationStatus status;
    private String title;

    public Notification(HangingTxDetected deserialize) {
        super();
        id = deserialize.getId();
        notificationId = new NotificationId();
        timestamp = deserialize.getTimestamp();
        title = "HANGING_TX";
        type = NotificationType.BELL;
        status = NotificationStatus.PENDING;
        descriptions = Collections.singleton(deserialize.getChangeId());
    }

    public Notification(NewUserRegistered event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "NEW_USER_REGISTER";
        type = NotificationType.BELL;
        status = NotificationStatus.PENDING;
        descriptions = Collections.singleton(event.getEmail().getEmail());
    }

    public Notification(ProjectOnboardingComplete event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "NEW_PROJECT_CREATED";
        type = NotificationType.BELL;
        status = NotificationStatus.PENDING;
        descriptions = Collections.singleton(event.getProjectName());
    }

    public Notification(ProxyCacheCheckFailedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        type = NotificationType.BELL;
        status = NotificationStatus.PENDING;
        title = "PROXY_CHECK_FAILED";
    }

    public Notification(CrossDomainValidationService.ValidationFailedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "VALIDATION_FAILED";
        type = NotificationType.BELL;
        status = NotificationStatus.PENDING;
        descriptions = Collections.singleton(event.getMessage());
    }

    public Notification(UserMfaNotificationEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "USER_MFA_REQUIRED";
        status = NotificationStatus.PENDING;
        type = NotificationType.SMS;
    }

    public Notification(UserPwdResetCodeUpdated event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "USER_PWD_RESET_CODE_UPDATED";
        status = NotificationStatus.PENDING;
        type = NotificationType.EMAIL;
    }

    public Notification(PendingUserActivationCodeUpdated event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "PENDING_USER_ACTIVATION_CODE_UPDATED";
        status = NotificationStatus.PENDING;
        type = NotificationType.EMAIL;
    }

    public Notification(CrossDomainValidationFailureCheck event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = "CROSS_DOMAIN_VALIDATION_FAILED";
        status = NotificationStatus.PENDING;
        type = NotificationType.EMAIL;
    }

    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
    }
}
