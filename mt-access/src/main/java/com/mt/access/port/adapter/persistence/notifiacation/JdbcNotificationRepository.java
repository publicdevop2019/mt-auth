package com.mt.access.port.adapter.persistence.notifiacation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationId;
import com.mt.access.domain.model.notification.NotificationQuery;
import com.mt.access.domain.model.notification.NotificationRepository;
import com.mt.access.domain.model.notification.NotificationStatus;
import com.mt.access.domain.model.notification.NotificationType;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcNotificationRepository implements NotificationRepository {

    private static final String INSERT_SQL = "INSERT INTO notification (" +
        "id, " +
        "descriptions, " +
        "domain_id, " +
        "timestamp, " +
        "title, " +
        "ack, " +
        "type, " +
        "status, " +
        "user_id" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String MARK_AS_DELIVERED =
        "UPDATE notification n SET n.status = 'DELIVERED' where n.domain_id = ?";
    private static final String MARK_ACK_SQL =
        "UPDATE notification n SET n.ack = true where n.domain_id = ?";
    private static final String MARK_USER_ACK_SQL =
        "UPDATE notification n SET n.ack = true where n.domain_id = ? AND n.user_id = ?";
    private static final String FIND_BY_DOMAIN_ID_SQL = "";
    private static final String FIND_ALL_BELL_NOTIFICATION_BY_USER_ID_SQL =
        "SELECT * FROM notification n WHERE n.user_id = ? AND n.type = 'BELL' ORDER BY n.timestamp DESC LIMIT ? OFFSET ?";
    private static final String COUNT_ALL_BELL_NOTIFICATION_BY_USER_ID_SQL =
        "SELECT COUNT(*) AS count FROM notification n WHERE n.user_id = ? AND n.type = 'BELL'";
    private static final String FIND_ALL_MGMT_SQL =
        "SELECT * FROM notification n WHERE n.user_id IS NULL ORDER BY n.timestamp DESC LIMIT ? OFFSET ?";
    private static final String COUNT_ALL_MGMT_SQL =
        "SELECT COUNT(*) AS count FROM notification n WHERE n.user_id IS NULL";
    private static final String FIND_ALL_BELL_NOTIFICATION_MGMT_SQL =
        "SELECT * FROM notification n WHERE n.user_id IS NULL AND n.type = 'BELL' ORDER BY n.timestamp DESC LIMIT ? OFFSET ?";
    private static final String COUNT_ALL_BELL_NOTIFICATION_MGMT_SQL =
        "SELECT COUNT(*) AS count FROM notification n WHERE n.user_id IS NULL AND n.type = 'BELL'";
    private static final String FIND_UN_ACK_MGMT_SQL =
        "SELECT * FROM notification n WHERE n.user_id IS NULL AND n.type = 'BELL' AND n.ack = false ORDER BY n.timestamp DESC LIMIT ? OFFSET ?";
    private static final String COUNT_UN_ACK_MGMT_SQL =
        "SELECT COUNT(*) AS count FROM notification n WHERE n.user_id IS NULL AND n.type = 'BELL' AND n.ack = false";
    private static final String FIND_UN_ACK_BELL_NOTIFICATION_BY_USER_ID_SQL =
        "SELECT * FROM notification n WHERE n.user_id = ? AND n.ack = false AND n.type = 'BELL' ORDER BY n.timestamp DESC LIMIT ? OFFSET ?";
    private static final String COUNT_UN_ACK_BELL_NOTIFICATION_BY_USER_ID_SQL =
        "SELECT COUNT(*) AS count FROM notification n WHERE n.user_id = ? AND n.ack = false AND n.type = 'BELL' ";

    @Override
    public void add(Notification notification) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                notification.getId(),
                Checker.notNullOrEmpty(notification.getDescriptions()) ?
                    String.join(",", notification.getDescriptions()) :
                    null
                ,
                notification.getNotificationId().getDomainId(),
                notification.getTimestamp(),
                notification.getTitle(),
                Boolean.FALSE,
                notification.getType().name(),
                notification.getStatus().name(),
                notification.getUserId() == null ? null : notification.getUserId().getDomainId()
            );
    }

    @Override
    public void acknowledge(NotificationId notificationId) {
        CommonDomainRegistry.getJdbcTemplate().update(MARK_ACK_SQL, notificationId.getDomainId());
    }

    @Override
    public void acknowledgeForUser(NotificationId notificationId, UserId userId) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(MARK_USER_ACK_SQL, notificationId.getDomainId(), userId.getDomainId());

    }

    @Override
    public SumPagedRep<Notification> notificationsOfQuery(NotificationQuery query) {
        if (Checker.notNull(query.getUserId())) {
            if (Checker.isTrue(query.getIsUnAck())) {
                return findUnAckUserBellNotification(query);
            }
            return findAllUserBellNotification(query);
        }
        if (Checker.isTrue(query.isBell())) {
            if (Checker.isTrue(query.getIsUnAck())) {
                return findUnAckMgmtBellNotification(query);
            } else {
                return findAllMgmtBellNotification(query);
            }
        } else {
            return findAllMgmtNotification(query);
        }
    }

    private SumPagedRep<Notification> findUnAckMgmtBellNotification(NotificationQuery query) {
        List<Notification> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_UN_ACK_MGMT_SQL,
                new RowMapper(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_UN_ACK_MGMT_SQL,
                new DatabaseUtility.ExtractCount());
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<Notification> findAllMgmtBellNotification(NotificationQuery query) {
        List<Notification> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_BELL_NOTIFICATION_MGMT_SQL,
                new RowMapper(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_ALL_BELL_NOTIFICATION_MGMT_SQL,
                new DatabaseUtility.ExtractCount());
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<Notification> findAllMgmtNotification(NotificationQuery query) {
        List<Notification> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_MGMT_SQL,
                new RowMapper(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_ALL_MGMT_SQL,
                new DatabaseUtility.ExtractCount());
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<Notification> findUnAckUserBellNotification(NotificationQuery query) {
        List<Notification> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_UN_ACK_BELL_NOTIFICATION_BY_USER_ID_SQL,
                new RowMapper(),
                query.getUserId().getDomainId(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_UN_ACK_BELL_NOTIFICATION_BY_USER_ID_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getUserId().getDomainId());
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<Notification> findAllUserBellNotification(NotificationQuery query) {
        List<Notification> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_BELL_NOTIFICATION_BY_USER_ID_SQL,
                new RowMapper(),
                query.getUserId().getDomainId(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_ALL_BELL_NOTIFICATION_BY_USER_ID_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getUserId().getDomainId());
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Notification query(NotificationId notificationId) {
        List<Notification> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                notificationId.getDomainId()
            );
        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public void markAsDelivered(NotificationId notificationId) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(MARK_AS_DELIVERED, notificationId.getDomainId());
    }

    private static class RowMapper implements ResultSetExtractor<List<Notification>> {

        @Override
        public List<Notification> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Notification> results = new ArrayList<>();
            long currentId = -1L;
            Notification result;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    String descriptions = rs.getString("descriptions");
                    LinkedHashSet<String> strings = null;
                    if (Checker.notNull(descriptions)) {
                        strings =
                            Arrays.stream(descriptions.split(",")).collect(
                                Collectors.toCollection(LinkedHashSet::new));
                    }
                    result = Notification.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        strings,
                        new NotificationId(rs.getString("domain_id")),
                        DatabaseUtility.getNullableLong(rs, "timestamp"),
                        rs.getString("title"),
                        DatabaseUtility.getNullableBoolean(rs, "ack"),
                        NotificationType.valueOf(rs.getString("type")),
                        NotificationStatus.valueOf(rs.getString("status")),
                        Checker.notNull(rs.getString("user_id")) ?
                            new UserId(rs.getString("user_id")) : null
                    );
                    results.add(result);
                    currentId = dbId;
                }
            } while (rs.next());
            return results;
        }
    }
}
