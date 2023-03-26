package com.mt.access.port.adapter.persistence.notifiacation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationId;
import com.mt.access.domain.model.notification.NotificationQuery;
import com.mt.access.domain.model.notification.NotificationRepository;
import com.mt.access.domain.model.notification.Notification_;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaNotificationRepository
    extends JpaRepository<Notification, Long>, NotificationRepository {

    default void add(Notification notification) {
        save(notification);
    }

    default void acknowledge(NotificationId id) {
        ackNotification(id);
    }

    default void acknowledgeForUser(NotificationId id, UserId userId) {
        ackNotificationUser(id, userId);
    }

    default Optional<Notification> notificationOfId(NotificationId notificationId) {
        return notificationsOfQuery(new NotificationQuery(notificationId)).findFirst();
    }

    @Modifying
    @Query("update #{#entityName} n set n.ack=true where n.notificationId = ?1")
    void ackNotification(NotificationId id);

    @Modifying
    @Query("update #{#entityName} n set n.ack=true where n.notificationId = ?1 AND n.userId= ?2")
    void ackNotificationUser(NotificationId id, UserId userId);

    default SumPagedRep<Notification> notificationsOfQuery(NotificationQuery query) {
        QueryUtility.QueryContext<Notification> queryContext =
            QueryUtility.prepareContext(Notification.class, query);
        Optional.ofNullable(query.getIsUnAck()).ifPresent(e -> QueryUtility
            .addBooleanEqualPredicate(
                false,
                Notification_.ACK, queryContext));
        Optional.ofNullable(query.getType()).ifPresent(e -> QueryUtility
            .addEnumLiteralEqualPredicate(
                e,
                Notification_.TYPE, queryContext));
        Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
            .addDomainIdInPredicate(
                e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                Notification_.NOTIFICATION_ID, queryContext));
        Optional.ofNullable(query.getUserId()).ifPresent(e -> QueryUtility
            .addDomainIdIsPredicate(
                e.getDomainId(),
                Notification_.USER_ID, queryContext));
        Order order = null;
        if (query.getSort().isTimestamp()) {
            order = QueryUtility.getOrder(Notification_.CREATED_AT, queryContext,
                query.getSort().isAsc());
        }
        if (query.getSort().isId()) {
            order = QueryUtility.getOrder(Notification_.NOTIFICATION_ID, queryContext,
                query.getSort().isAsc());
        }
        queryContext.setOrder(order);
        return QueryUtility.nativePagedQuery(query, queryContext);
    }
}
