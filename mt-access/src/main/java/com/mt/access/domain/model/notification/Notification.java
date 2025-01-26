package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailed;
import com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning;
import com.mt.access.domain.model.sub_request.event.SubscribedEndpointExpired;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.access.domain.model.user.event.UserMfaNotification;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceived;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceived;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.job.event.JobNotFound;
import com.mt.common.domain.model.job.event.JobPaused;
import com.mt.common.domain.model.job.event.JobStarving;
import com.mt.common.domain.model.job.event.JobThreadStarving;
import com.mt.common.domain.model.validate.Utility;
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
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = deserialize.getTimestamp();
        title = "HANGING_TX";
        type = NotificationType.BELL;
        descriptions.add(deserialize.getChangeId());
    }

    public Notification(NewUserRegistered event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = NewUserRegistered.name;
        type = NotificationType.BELL;
        descriptions.add(event.getRegisteredUsing());
    }

    public Notification(ProjectOnboardingComplete event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = ProjectOnboardingComplete.name;
        type = NotificationType.BELL;
        descriptions.add(event.getProjectName());
    }

    public Notification(ProxyCacheCheckFailed event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        type = NotificationType.BELL;
        title = ProxyCacheCheckFailed.name;
    }

    public Notification(CrossDomainValidationService.ValidationFailed event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = CrossDomainValidationService.ValidationFailed.name;
        type = NotificationType.BELL;
        descriptions.addAll(event.getMessage());
    }

    public Notification(UserMfaNotification event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = UserMfaNotification.name;
        type = NotificationType.SMS;
    }

    public Notification(UserPwdResetCodeUpdated event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = UserPwdResetCodeUpdated.name;
        type = NotificationType.EMAIL;
    }

    public Notification(VerificationCodeUpdated event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = VerificationCodeUpdated.name;
        if (Utility.isNull(event.getEmail())) {
            type = NotificationType.SMS;
        } else {
            type = NotificationType.EMAIL;
        }
    }

    public Notification(CrossDomainValidationFailureCheck event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = CrossDomainValidationFailureCheck.name;
        type = NotificationType.EMAIL;
    }

    public Notification(UnrountableMsgReceived event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = UnrountableMsgReceived.name;
        type = NotificationType.BELL;
        descriptions.add(String.valueOf(event.getSourceEventId()));
        descriptions.add(event.getSourceTopic());
        descriptions.add(event.getSourceName());
    }

    public Notification(SubscribedEndpointExpired event,
                        DomainId e) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = SubscribedEndpointExpired.name;
        type = NotificationType.BELL;
        userId = new UserId(e.getDomainId());
    }


    public Notification(JobPaused event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = JobPaused.name;
        type = NotificationType.BELL;
    }

    public Notification(JobNotFound event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = JobNotFound.name;
        descriptions.add(event.getJobName());
        type = NotificationType.BELL;
    }

    public Notification(JobThreadStarving event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = JobThreadStarving.name;
        descriptions.add(event.getJobName());
        descriptions.add(String.valueOf(event.getInstanceId()));
        type = NotificationType.BELL;
    }

    public Notification(JobStarving event) {
        super();
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        descriptions.add(event.getJobName());
        title = JobStarving.name;
        type = NotificationType.BELL;
    }

    public Notification(RejectedMsgReceived event) {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        notificationId = new NotificationId();
        timestamp = event.getTimestamp();
        title = RejectedMsgReceived.name;
        type = NotificationType.BELL;
        descriptions.add(String.valueOf(event.getSourceEventId()));
        descriptions.add(event.getSourceTopic());
        descriptions.add(event.getSourceName());
    }

    public Notification(RawAccessRecordProcessingWarning event) {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
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
