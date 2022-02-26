package com.mt.access.domain.model.system_notification;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;

public interface SystemNotificationRepository {
    void add(SystemNotification notification);

    SumPagedRep<SystemNotification> latestSystemNotifications(PageConfig defaultPaging, QueryConfig queryConfig);
}
