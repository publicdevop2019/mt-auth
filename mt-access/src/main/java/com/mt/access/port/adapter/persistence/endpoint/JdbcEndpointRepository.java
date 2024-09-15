package com.mt.access.port.adapter.persistence.endpoint;

import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.EndpointRepository;
import com.mt.access.domain.model.permission.PermissionId;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEndpointRepository implements EndpointRepository {
    private static final String INSERT_SQL = "INSERT INTO endpoint " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "cache_profile_id, " +
        "client_id, " +
        "cors_profile_id, " +
        "csrf_enabled, " +
        "description, " +
        "domain_id, " +
        "websocket, " +
        "method, " +
        "name, " +
        "path, " +
        "permission_id, " +
        "project_id, " +
        "secured, " +
        "shared, " +
        "expire_reason, " +
        "expired, " +
        "external, " +
        "replenish_rate, " +
        "burst_capacity" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM endpoint e WHERE e.id = ?";
    private static final String BATCH_DELETE_BY_IDS_SQL =
        "DELETE FROM endpoint e WHERE e.id IN (%s)";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM endpoint e WHERE e.domain_id = ?";
    private static final String DYNAMIC_DATA_QUERY_SQL = "SELECT * FROM endpoint e WHERE %s ORDER BY e.id ASC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM endpoint e WHERE %s";
    private static final String CHECK_DUPLICATE_SQL =
        "SELECT COUNT(*) AS count FROM endpoint e WHERE e.client_id = ? AND e.path = ? AND e.method = ?";
    private static final String FIND_ALL_CACHE_PROFILE_ID_SQL =
        "SELECT DISTINCT e.cache_profile_id FROM endpoint e WHERE e.cache_profile_id IS NOT NULL";
    private static final String FIND_ALL_CORS_PROFILE_ID_SQL =
        "SELECT DISTINCT e.cors_profile_id FROM endpoint e WHERE e.cors_profile_id IS NOT NULL";
    private static final String FIND_ALL_CLIENT_ID_SQL =
        "SELECT DISTINCT e.client_id FROM endpoint e";
    private static final String COUNT_PROJECT_TOTAL_SQL =
        "SELECT COUNT(*) AS count FROM endpoint e " +
            "WHERE e.project_id = ?";
    private static final String COUNT_PUBLIC_TOTAL_SQL =
        "SELECT COUNT(*) AS count FROM endpoint e " +
            "WHERE e.secured = 0 AND e.external = 1";
    private static final String COUNT_SHARED_TOTAL_SQL =
        "SELECT COUNT(*) AS count FROM endpoint e " +
            "WHERE e.shared = 1";
    private static final String COUNT_TOTAL_SQL = "SELECT COUNT(*) AS count FROM endpoint";
    private static final String UPDATE_SQL = "UPDATE endpoint e SET " +
        "e.modified_at = ? ," +
        "e.modified_by = ?, " +
        "e.version = ?, " +
        "e.cache_profile_id = ?, " +
        "e.cors_profile_id = ?, " +
        "e.csrf_enabled = ?, " +
        "e.description = ?, " +
        "e.domain_id = ?, " +
        "e.websocket = ?, " +
        "e.method = ?, " +
        "e.name = ?, " +
        "e.path = ?, " +
        "e.expire_reason = ?, " +
        "e.expired = ?, " +
        "e.replenish_rate = ?, " +
        "e.burst_capacity = ? " +
        "WHERE e.id = ? AND e.version = ? ";

    @Override
    public Endpoint query(EndpointId endpointId) {
        List<Endpoint> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                endpointId.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public void update(Endpoint old, Endpoint update) {
        if (old.sameAs(update)) {
            return;
        }
        CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                update.getModifiedAt(),
                update.getModifiedBy(),
                update.getVersion() + 1,
                Checker.isNull(update.getCacheProfileId()) ? null : update.getCacheProfileId().getDomainId(),
                Checker.isNull(update.getCorsProfileId()) ? null : update.getCorsProfileId().getDomainId(),
                update.getCsrfEnabled(),
                update.getDescription(),
                update.getEndpointId().getDomainId(),
                update.getWebsocket(),
                update.getMethod(),
                update.getName(),
                update.getPath(),
                update.getExpireReason(),
                update.getExpired(),
                update.getReplenishRate(),
                update.getBurstCapacity(),
                update.getId(),
                update.getVersion()
            );
    }

    @Override
    public void add(Endpoint endpoint) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                endpoint.getId(),
                endpoint.getCreatedAt(),
                endpoint.getCreatedBy(),
                endpoint.getModifiedAt(),
                endpoint.getModifiedBy(),
                0,
                Checker.isNull(endpoint.getCacheProfileId()) ? null : endpoint.getCacheProfileId().getDomainId(),
                endpoint.getClientId().getDomainId(),
                Checker.isNull(endpoint.getCorsProfileId()) ? null : endpoint.getCorsProfileId().getDomainId(),
                endpoint.getCsrfEnabled(),
                endpoint.getDescription(),
                endpoint.getEndpointId().getDomainId(),
                endpoint.getWebsocket(),
                endpoint.getMethod(),
                endpoint.getName(),
                endpoint.getPath(),
                Checker.isNull(endpoint.getPermissionId()) ? null : endpoint.getPermissionId().getDomainId(),
                endpoint.getProjectId().getDomainId(),
                endpoint.getSecured(),
                endpoint.getShared(),
                endpoint.getExpireReason(),
                endpoint.getExpired(),
                endpoint.getExternal(),
                endpoint.getReplenishRate(),
                endpoint.getBurstCapacity()
            );
    }

    @Override
    public void remove(Endpoint endpoint) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_BY_ID_SQL,
                endpoint.getId()
            );
    }

    @Override
    public void remove(Set<Endpoint> endpoints) {
        String inClause = DatabaseUtility.getInClause(endpoints.size());
        CommonDomainRegistry.getJdbcTemplate()
            .update(String.format(BATCH_DELETE_BY_IDS_SQL, inClause),
                endpoints.stream().map(Auditable::getId).toArray()
            );
    }

    @Override
    public SumPagedRep<Endpoint> query(EndpointQuery query) {
        List<String> whereClause = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getEndpointIds())) {
            String inClause = DatabaseUtility.getInClause(query.getEndpointIds().size());
            String byDomainIds = String.format("e.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getClientIds())) {
            String inClause = DatabaseUtility.getInClause(query.getClientIds().size());
            String byDomainIds = String.format("e.client_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getProjectIds())) {
            String inClause = DatabaseUtility.getInClause(query.getProjectIds().size());
            String byDomainIds = String.format("e.project_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getPermissionIds())) {
            String inClause = DatabaseUtility.getInClause(query.getPermissionIds().size());
            String byDomainIds = String.format("e.permission_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getCacheProfileIds())) {
            String inClause = DatabaseUtility.getInClause(query.getCacheProfileIds().size());
            String byDomainIds = String.format("e.cache_profile_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getCorsProfileIds())) {
            String inClause = DatabaseUtility.getInClause(query.getCorsProfileIds().size());
            String byDomainIds = String.format("e.cors_profile_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNull(query.getPath())) {
            String path = "e.path = ?";
            whereClause.add(path);
        }
        if (Checker.notNull(query.getMethod())) {
            String method = "e.method = ?";
            whereClause.add(method);
        }
        if (Checker.notNull(query.getIsWebsocket())) {
            String websocket =
                query.getIsWebsocket() ? "e.websocket = 1" : "e.websocket = 0";
            whereClause.add(websocket);
        }
        if (Checker.notNull(query.getIsShared()) && query.getIsShared()) {
            whereClause.add("((e.external = 1 AND e.secured = 0) OR e.shared = 1)");
        }
        if (Checker.notNull(query.getIsSecured())) {
            String secured =
                query.getIsSecured() ? "e.secured = 1" : "e.secured = 0";
            whereClause.add(secured);
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
        if (Checker.notNullOrEmpty(query.getEndpointIds())) {
            args.addAll(
                query.getEndpointIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNull(query.getClientIds())) {
            args.addAll(
                query.getClientIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNullOrEmpty(query.getProjectIds())) {
            args.addAll(
                query.getProjectIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNullOrEmpty(query.getPermissionIds())) {
            args.addAll(
                query.getPermissionIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNullOrEmpty(query.getCacheProfileIds())) {
            args.addAll(
                query.getCacheProfileIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNullOrEmpty(query.getCorsProfileIds())) {
            args.addAll(
                query.getCorsProfileIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNull(query.getPath())) {
            args.add(query.getPath());
        }
        if (Checker.notNull(query.getMethod())) {
            args.add(query.getMethod());
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
        List<Endpoint> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Set<CacheProfileId> getCacheProfileIds() {
        List<CacheProfileId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_CACHE_PROFILE_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<CacheProfileId> list = new ArrayList<>();
                    do {
                        list.add(new CacheProfileId(rs.getString("cache_profile_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public Set<CorsProfileId> getCorsProfileIds() {
        List<CorsProfileId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_CORS_PROFILE_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<CorsProfileId> list = new ArrayList<>();
                    do {
                        list.add(new CorsProfileId(rs.getString("cors_profile_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public Set<ClientId> getClientIds() {
        List<ClientId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_CLIENT_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<ClientId> list = new ArrayList<>();
                    do {
                        list.add(new ClientId(rs.getString("client_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public long countTotal() {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_TOTAL_SQL,
                new DatabaseUtility.ExtractCount()
            );
        return query;
    }

    @Override
    public long countSharedTotal() {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_SHARED_TOTAL_SQL,
                new DatabaseUtility.ExtractCount()
            );
        return query;
    }

    @Override
    public long countPublicTotal() {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PUBLIC_TOTAL_SQL,
                new DatabaseUtility.ExtractCount()
            );
        return query;
    }

    @Override
    public long countProjectTotal(ProjectId projectId) {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PROJECT_TOTAL_SQL,
                new DatabaseUtility.ExtractCount(),
                projectId.getDomainId()
            );
        return query;
    }

    @Override
    public boolean checkDuplicate(ClientId clientId, String path, String method) {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                CHECK_DUPLICATE_SQL,
                new DatabaseUtility.ExtractCount(),
                clientId.getDomainId(),
                path,
                method
            );
        return query > 0;
    }


    private static class RowMapper implements ResultSetExtractor<List<Endpoint>> {

        @Override
        public List<Endpoint> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Endpoint> list = new ArrayList<>();
            long currentId = -1L;
            Endpoint endpoint = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    endpoint = Endpoint.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        Checker.notNull(rs.getString("cache_profile_id")) ?
                            new CacheProfileId(rs.getString("cache_profile_id")) : null,
                        new ClientId(rs.getString("client_id")),
                        Checker.notNull(rs.getString("cors_profile_id")) ?
                            new CorsProfileId(rs.getString("cors_profile_id")) : null,
                        DatabaseUtility.getNullableBoolean(rs, "csrf_enabled"),
                        rs.getString("description"),
                        new EndpointId(rs.getString("domain_id")),
                        DatabaseUtility.getNullableBoolean(rs, "websocket"),
                        rs.getString("method"),
                        rs.getString("name"),
                        rs.getString("path"),
                        Checker.notNull(rs.getString("permission_id")) ?
                            new PermissionId(rs.getString("permission_id")) : null,
                        new ProjectId(rs.getString("project_id")),
                        DatabaseUtility.getNullableBoolean(rs, "secured"),
                        DatabaseUtility.getNullableBoolean(rs, "shared"),
                        rs.getString("expire_reason"),
                        DatabaseUtility.getNullableBoolean(rs, "expired"),
                        DatabaseUtility.getNullableBoolean(rs, "external"),
                        DatabaseUtility.getNullableInteger(rs, "replenish_rate"),
                        DatabaseUtility.getNullableInteger(rs, "burst_capacity")
                    );
                    list.add(endpoint);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
