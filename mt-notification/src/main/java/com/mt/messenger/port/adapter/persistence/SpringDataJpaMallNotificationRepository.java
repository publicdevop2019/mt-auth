package com.mt.messenger.port.adapter.persistence;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import com.mt.messenger.domain.model.mall_notification.MallNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaMallNotificationRepository extends JpaRepository<MallNotification, Long>, MallNotificationRepository {
    Page<MallNotification> findAll(Pageable pageable);

    default void add(MallNotification notification) {
        save(notification);
    }

    default SumPagedRep<MallNotification> latestMallNotifications(PageConfig defaultPaging, QueryConfig queryConfig) {
        Pageable sortedByTimestampDesc =
                PageRequest.of(defaultPaging.getPageNumber().intValue(), defaultPaging.getPageSize(), Sort.by("timestamp").descending());
        Page<MallNotification> all = findAll(sortedByTimestampDesc);
        SumPagedRep<MallNotification> systemNotificationSumPagedRep = new SumPagedRep<>();
        if (!all.getContent().isEmpty())
            systemNotificationSumPagedRep.setData(all.getContent());
        systemNotificationSumPagedRep.setTotalItemCount(all.getTotalElements());
        return systemNotificationSumPagedRep;
    }
}
