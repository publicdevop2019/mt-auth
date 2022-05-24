package com.mt.access.port.adapter.persistence.notifiacation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationId;
import com.mt.access.domain.model.notification.NotificationQuery;
import com.mt.access.domain.model.notification.NotificationRepository;
import com.mt.access.domain.model.notification.Notification_;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
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

    @Modifying
    @Query("update #{#entityName} n set n.ack=true where n.notificationId = ?1")
    void ackNotification(NotificationId id);

    default SumPagedRep<Notification> latestNotifications(NotificationQuery query) {
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
        Order order = null;
        if (query.getSort().isTimestamp()) {
            order = QueryUtility.getOrder(Notification_.CREATED_AT, queryContext,
                query.getSort().isAsc());
        }
        queryContext.setOrder(order);
        return QueryUtility.pagedQuery(query, queryContext);
    }
}
