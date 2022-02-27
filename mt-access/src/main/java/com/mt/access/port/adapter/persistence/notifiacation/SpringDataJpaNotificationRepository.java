package com.mt.access.port.adapter.persistence.notifiacation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationRepository;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaNotificationRepository extends JpaRepository<Notification, Long>, NotificationRepository {

    default void add(Notification notification) {
        save(notification);
    }

    default SumPagedRep<Notification> latestNotifications(PageConfig defaultPaging, QueryConfig queryConfig) {
        Pageable sortedByTimestampDesc =
                PageRequest.of(defaultPaging.getPageNumber().intValue(), defaultPaging.getPageSize(), Sort.by("timestamp").descending());
        Page<Notification> all = findAll(sortedByTimestampDesc);
        SumPagedRep<Notification> systemNotificationSumPagedRep = new SumPagedRep<>();
        if (!all.getContent().isEmpty())
            systemNotificationSumPagedRep.setData(all.getContent());
        systemNotificationSumPagedRep.setTotalItemCount(all.getTotalElements());
        return systemNotificationSumPagedRep;
    }
}
