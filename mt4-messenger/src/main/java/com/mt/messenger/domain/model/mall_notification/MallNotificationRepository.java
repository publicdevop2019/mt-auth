package com.mt.messenger.domain.model.mall_notification;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;

public interface MallNotificationRepository {
    void add(MallNotification notification);

    SumPagedRep<MallNotification> latestMallNotifications(PageConfig defaultPaging, QueryConfig queryConfig);
}
