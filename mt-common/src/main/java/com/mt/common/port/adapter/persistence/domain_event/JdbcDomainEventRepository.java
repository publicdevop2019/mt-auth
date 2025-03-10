package com.mt.common.port.adapter.persistence.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventRepository;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcDomainEventRepository implements DomainEventRepository {

    private static final String INSERT_SQL = "INSERT INTO stored_event " +
        "(" +
        "id, " +
        "domain_id, " +
        "event_body, " +
        "name, " +
        "timestamp, " +
        "topic, " +
        "send, " +
        "routable, " +
        "rejected, " +
        "trace_id" +
        ") VALUES" +
        "(?,?,?,?,?,?,?,?,?,?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM stored_event se WHERE se.id = ?";
    private static final String UPDATE_SQL = "UPDATE stored_event se SET " +
        "se.rejected = ? ," +
        "se.send = ?, " +
        "se.routable = ? " +
        "WHERE se.id = ?";
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT * FROM stored_event se WHERE %s ORDER BY se.id DESC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM stored_event se WHERE %s";

    @Override
    public void append(StoredEvent event) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                event.getId(),
                event.getDomainId(),
                event.getEventBody(),
                event.getName(),
                event.getTimestamp(),
                event.getTopic(),
                event.getSend(),
                event.getRoutable(),
                event.getRejected(),
                event.getTraceId()
            );
    }

    @Override
    public StoredEvent getById(long id) {
        List<StoredEvent> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_ID_SQL,
                new RowMapper(),
                id
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public SumPagedRep<StoredEvent> query(StoredEventQuery query) {

        List<String> whereClause = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getIds())) {
            String inClause = DatabaseUtility.getInClause(query.getIds().size());
            String byIds = String.format("se.id IN (%s)", inClause);
            whereClause.add(byIds);
        }
        if (Checker.notNullOrEmpty(query.getNames())) {
            String inClause = DatabaseUtility.getInClause(query.getNames().size());
            String byNames = String.format("se.name IN (%s)", inClause);
            whereClause.add(byNames);
        }
        if (Checker.notNullOrEmpty(query.getDomainIds())) {
            String inClause = DatabaseUtility.getInClause(query.getDomainIds().size());
            String byIds = String.format("se.domain_id IN (%s)", inClause);
            whereClause.add(byIds);
        }
        if (Checker.notNull(query.getSend())) {
            whereClause.add(query.getSend() ? "se.send = 1" : "se.send = 0");
        }
        if (Checker.notNull(query.getRoutable())) {
            whereClause.add(query.getRoutable() ? "se.routable = 1" : "se.routable = 0");
        }
        if (Checker.notNull(query.getRejected())) {
            whereClause.add(query.getRejected() ? "se.rejected = 1" : "se.rejected = 0");
        }
        String join = String.join(" AND ", whereClause);
        String finalDataQuery;
        String finalCountQuery;
        if (!whereClause.isEmpty()) {
            finalDataQuery = String.format(DYNAMIC_DATA_QUERY_SQL, join);
            finalCountQuery = String.format(DYNAMIC_COUNT_QUERY_SQL, join);
        } else {
            finalDataQuery = DYNAMIC_DATA_QUERY_SQL.replace(" WHERE %s", "");
            finalCountQuery = DYNAMIC_COUNT_QUERY_SQL.replace(" WHERE %s", "");
        }
        List<Object> args = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getIds())) {
            args.addAll(
                query.getIds());
        }
        if (Checker.notNullOrEmpty(query.getNames())) {
            args.addAll(
                query.getNames());
        }
        if (Checker.notNullOrEmpty(query.getDomainIds())) {
            args.addAll(
                query.getDomainIds());
        }
        Long count;
        if (args.isEmpty()) {
            count = CommonDomainRegistry.getJdbcTemplate()
                .query(finalCountQuery,
                    new DatabaseUtility.ExtractCount()
                );
        } else {
            count = CommonDomainRegistry.getJdbcTemplate()
                .query(finalCountQuery,
                    new DatabaseUtility.ExtractCount(),
                    args.toArray()
                );
        }
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<StoredEvent> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public void update(StoredEvent old, StoredEvent updated) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getRejected(),
                updated.getSend(),
                updated.getRoutable(),
                updated.getId()
            );
        DatabaseUtility.checkUpdate(update);
    }


    private static class RowMapper implements ResultSetExtractor<List<StoredEvent>> {

        @Override
        public List<StoredEvent> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<StoredEvent> list = new ArrayList<>();
            long currentId = -1L;
            StoredEvent event;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    event = StoredEvent.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        rs.getString("domain_id"),
                        rs.getString("event_body"),
                        rs.getString("name"),
                        DatabaseUtility.getNullableLong(rs, "timestamp"),
                        rs.getString("topic"),
                        DatabaseUtility.getNullableBoolean(rs, "send"),
                        DatabaseUtility.getNullableBoolean(rs, "routable"),
                        DatabaseUtility.getNullableBoolean(rs, "rejected"),
                        rs.getString("trace_id")
                    );
                    list.add(event);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
