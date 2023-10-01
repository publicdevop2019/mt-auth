package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.pending_user.event.PendingUserCreated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning;
import com.mt.access.domain.model.sub_request.event.SubscriberEndpointExpireEvent;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceivedEvent;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.domain.model.job.event.JobPausedEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
import com.mt.common.domain.model.job.event.JobThreadStarvingEvent;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {
    @Setter
    @Getter
    protected Long id;
    @Setter(AccessLevel.PRIVATE)
    private Boolean ack = false;
    @Setter(AccessLevel.PRIVATE)
    private NotificationId notificationId;
    @Setter(AccessLevel.PRIVATE)
    private Long timestamp;
    @Setter(AccessLevel.PRIVATE)
    private LinkedHashSet<String> descriptions = new LinkedHashSet<>();
    @Setter(AccessLevel.PRIVATE)
    private NotificationType type;
    @Setter(AccessLevel.PRIVATE)
    private NotificationStatus status = NotificationStatus.PENDING;
    @Setter(AccessLevel.PRIVATE)
    private String title;
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;

    public Notification(HangingTxDetected deserialize) {
        super();
        id = deserialize.getId();
        notificationId = new NotificationId();
        timestamp = deserialize.getTimestamp();
        title = "HANGING_TX";
        type = NotificationType.BELL;
        descriptions.add(deserialize.getChangeId());
    }

    public Notification(NewUserRegistered event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = NewUserRegistered.name;
        type = NotificationType.BELL;
        descriptions.add(event.getEmail().getEmail());
    }

    public Notification(ProjectOnboardingComplete event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = ProjectOnboardingComplete.name;
        type = NotificationType.BELL;
        descriptions.add(event.getProjectName());
    }

    public Notification(ProxyCacheCheckFailedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        type = NotificationType.BELL;
        title = ProxyCacheCheckFailedEvent.name;
    }

    public Notification(CrossDomainValidationService.ValidationFailedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = CrossDomainValidationService.ValidationFailedEvent.name;
        type = NotificationType.BELL;
        descriptions.addAll(event.getMessage());
    }

    public Notification(UserMfaNotificationEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = UserMfaNotificationEvent.name;
        type = NotificationType.SMS;
    }

    public Notification(UserPwdResetCodeUpdated event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = UserPwdResetCodeUpdated.name;
        type = NotificationType.EMAIL;
    }

    public Notification(PendingUserActivationCodeUpdated event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = PendingUserActivationCodeUpdated.name;
        type = NotificationType.EMAIL;
    }

    public Notification(CrossDomainValidationFailureCheck event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = CrossDomainValidationFailureCheck.name;
        type = NotificationType.EMAIL;
    }

    public Notification(UnrountableMsgReceivedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = UnrountableMsgReceivedEvent.name;
        type = NotificationType.BELL;
        descriptions.add(String.valueOf(event.getSourceEventId()));
        descriptions.add(event.getSourceTopic());
        descriptions.add(event.getSourceName());
    }

    public Notification(SubscriberEndpointExpireEvent event,
                        DomainId e) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = SubscriberEndpointExpireEvent.name;
        type = NotificationType.BELL;
        userId = new UserId(e.getDomainId());
    }


    public Notification(JobPausedEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = JobPausedEvent.name;
        type = NotificationType.BELL;
    }

    public Notification(JobNotFoundEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = JobNotFoundEvent.name;
        descriptions.add(event.getJobName());
        type = NotificationType.BELL;
    }

    public Notification(JobThreadStarvingEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = JobThreadStarvingEvent.name;
        descriptions.add(event.getJobName());
        descriptions.add(String.valueOf(event.getInstanceId()));
        type = NotificationType.BELL;
    }

    public Notification(JobStarvingEvent event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        descriptions.add(event.getJobName());
        title = JobStarvingEvent.name;
        type = NotificationType.BELL;
    }

    public Notification(PendingUserCreated event) {
        super();
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = event.getName();
        descriptions.add(event.getDomainId().getDomainId());
        type = NotificationType.BELL;
    }

    public Notification(RejectedMsgReceivedEvent event) {
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = RejectedMsgReceivedEvent.name;
        type = NotificationType.BELL;
        descriptions.add(String.valueOf(event.getSourceEventId()));
        descriptions.add(event.getSourceTopic());
        descriptions.add(event.getSourceName());
    }

    public Notification(RawAccessRecordProcessingWarning event) {
        id = event.getId();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = RawAccessRecordProcessingWarning.name;
        type = NotificationType.BELL;
        if (event.getIssueIds().size() > 3) {
            descriptions.addAll(event.getIssueIds().stream().limit(2).collect(Collectors.toSet()));
            descriptions.add("CHECK_EVENT_FOR_MORE");
        } else {
            descriptions.addAll(event.getIssueIds());
        }

    }

    public static Notification fromDatabaseRow(Long id, LinkedHashSet<String> descriptions,
                                               NotificationId domainId, Long timestamp,
                                               String title, Boolean ack,
                                               NotificationType type, NotificationStatus status,
                                               UserId userId) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setDescriptions(descriptions);
        notification.setNotificationId(domainId);
        notification.setTimestamp(timestamp);
        notification.setTitle(title);
        notification.setAck(ack);
        notification.setType(type);
        notification.setStatus(status);
        notification.setUserId(userId);
        return notification;
    }
}
