package com.mt.access.port.adapter.persistence.endpoint;

import com.mt.access.domain.model.endpoint.ExternalUrl;
import com.mt.access.domain.model.endpoint.Router;
import com.mt.access.domain.model.endpoint.RouterId;
import com.mt.access.domain.model.endpoint.RouterQuery;
import com.mt.access.domain.model.endpoint.RouterRepository;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRouterRepository implements RouterRepository {
    private static final String INSERT_SQL = "INSERT INTO router " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "name, " +
        "description, " +
        "domain_id, " +
        "path, " +
        "external_url, " +
        "project_id " +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM router e WHERE e.domain_id = ?";
    private static final String UPDATE_SQL = "UPDATE router e SET " +
        "e.modified_at = ? ," +
        "e.modified_by = ?, " +
        "e.version = ?, " +
        "e.name = ?, " +
        "e.description = ?, " +
        "e.path = ?, " +
        "e.external_url = ? " +
        "WHERE e.id = ? AND e.version = ? ";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM router e WHERE e.id = ?";
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT * FROM router e WHERE %s ORDER BY e.id ASC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM router e WHERE %s";

    @Override
    public Router query(RouterId routerId) {
        List<Router> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                routerId.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public void update(Router old, Router update) {
        if (old.sameAs(update)) {
            return;
        }
        CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                update.getModifiedAt(),
                update.getModifiedBy(),
                update.getVersion() + 1,
                update.getName(),
                update.getDescription(),
                update.getPath(),
                update.getExternalUrl().getValue(),
                update.getId(),
                update.getVersion()
            );
    }

    @Override
    public void add(Router router) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                router.getId(),
                router.getCreatedAt(),
                router.getCreatedBy(),
                router.getModifiedAt(),
                router.getModifiedBy(),
                0,
                router.getName(),
                router.getDescription(),
                router.getRouterId().getDomainId(),
                router.getPath(),
                router.getExternalUrl().getValue(),
                router.getProjectId().getDomainId()
            );
    }

    @Override
    public void remove(Router router) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_BY_ID_SQL,
                router.getId()
            );
    }

    @Override
    public SumPagedRep<Router> query(RouterQuery query) {
        List<String> whereClause = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getRouterIds())) {
            String inClause = DatabaseUtility.getInClause(query.getRouterIds().size());
            String byDomainIds = String.format("e.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getProjectIds())) {
            String inClause = DatabaseUtility.getInClause(query.getProjectIds().size());
            String byDomainIds = String.format("e.project_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
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
        if (Checker.notNullOrEmpty(query.getRouterIds())) {
            args.addAll(
                query.getRouterIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNullOrEmpty(query.getProjectIds())) {
            args.addAll(
                query.getProjectIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
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
        List<Router> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    private static class RowMapper implements ResultSetExtractor<List<Router>> {

        @Override
        public List<Router> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Router> list = new ArrayList<>();
            long currentId = -1L;
            Router router = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    router = Router.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        new RouterId(rs.getString("domain_id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("path"),
                        new ProjectId(rs.getString("project_id")),
                        new ExternalUrl(rs.getString("external_url"))
                    );
                    list.add(router);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
