package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NotificationQuery extends QueryCriteria {
    private static final String UN_ACK = "unAck";
    private final Sort sort;
    private Set<NotificationType> type;
    private Set<NotificationId> ids;
    private Boolean isUnAck;
    private UserId userId;

    public NotificationQuery(NotificationType type, String queryParam, String pageParam,
                             String skipCount) {
        updateQueryParam(queryParam);
        if (type != null) {
            this.type = Collections.singleton(type);
        }
        setPageConfig(PageConfig.limited(pageParam, 200));
        setQueryConfig(new QueryConfig(skipCount));
        this.sort = Sort.byLatestTimestamp();
    }

    public NotificationQuery(String queryParam, String pageParam,
                             String skipCount) {
        this(null, queryParam, pageParam, skipCount);
    }

    public NotificationQuery(NotificationId notificationId) {
        ids = Collections.singleton(notificationId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId();
    }

    public static NotificationQuery queryForUser(String queryParam, String pageParam,
                                                 String skipCount, UserId userId) {
        NotificationQuery notificationQuery =
            new NotificationQuery(null, queryParam, pageParam, skipCount);
        Validator.notNull(userId);
        notificationQuery.userId = userId;
        return notificationQuery;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            UN_ACK);
        Optional.ofNullable(stringStringMap.get(UN_ACK)).ifPresent(e -> {
            isUnAck = Boolean.TRUE;
        });
    }

    @Getter
    public static class Sort {
        private Boolean isAsc;
        private Boolean isTimestamp = false;
        private Boolean isId = false;

        public static Sort byLatestTimestamp() {
            Sort sort = new Sort();
            sort.isTimestamp = true;
            sort.isAsc = false;
            return sort;
        }

        public static Sort byId() {
            Sort sort = new Sort();
            sort.isId = true;
            sort.isAsc = false;
            return sort;
        }
    }
}
