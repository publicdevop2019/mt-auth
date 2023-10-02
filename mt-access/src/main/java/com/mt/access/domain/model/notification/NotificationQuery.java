package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class NotificationQuery extends QueryCriteria {
    private static final String UN_ACK = "unAck";
    private boolean isBell = false;
    private Boolean isUnAck;
    private UserId userId;

    private NotificationQuery(boolean isBell, String queryParam, String pageParam,
                              String skipCount) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 200));
        setQueryConfig(new QueryConfig(skipCount));
        this.isBell = isBell;
    }

    public static NotificationQuery queryUserBell(String queryParam, String pageParam,
                                                  String skipConfig, UserId userId) {
        Validator.notNull(userId);
        NotificationQuery notificationQuery =
            new NotificationQuery(true, queryParam, pageParam, skipConfig);
        notificationQuery.userId = userId;
        return notificationQuery;
    }

    public static NotificationQuery queryMgmtBell(String queryParam, String pageParam,
                                                  String skipConfig) {
        return new NotificationQuery(true, queryParam, pageParam, skipConfig);
    }

    public static NotificationQuery queryMgmt(String pageParam, String skipConfig) {
        return new NotificationQuery(false, null, pageParam, skipConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            UN_ACK);
        Optional.ofNullable(stringStringMap.get(UN_ACK)).ifPresent(e -> {
            isUnAck = Boolean.TRUE;
        });
    }
}
