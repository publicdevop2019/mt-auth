package com.mt.access.domain.model.notification;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;

public interface NotificationRepository {
    void add(Notification notification);

    SumPagedRep<Notification> latestNotifications(PageConfig defaultPaging,
                                                  QueryConfig queryConfig);
}
