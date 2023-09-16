package com.mt.access.port.adapter.persistence.cache_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.cache_profile.CacheProfileRepository;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCacheProfileRepository implements CacheProfileRepository {
    private static final String INSERT_SQL = "INSERT INTO cache_profile " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "allow_cache, " +
        "domain_id, " +
        "description, " +
        "etag, " +
        "expires, " +
        "max_age, " +
        "name, " +
        "smax_age, " +
        "vary, " +
        "weak_validation, " +
        "project_id" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE cache_profile cp SET " +
        "cp.modified_at = ? ," +
        "cp.modified_by = ?, " +
        "cp.version = ?, " +
        "cp.allow_cache = ?, " +
        "cp.domain_id = ?, " +
        "cp.description = ?, " +
        "cp.etag = ?, " +
        "cp.expires = ?, " +
        "cp.max_age = ?, " +
        "cp.name = ?, " +
        "cp.smax_age = ?, " +
        "cp.vary = ?, " +
        "cp.weak_validation = ? WHERE cp.id = ? AND cp.version = ? ";
    private static final String INSERT_MAP_SQL = "INSERT INTO cache_control_map " +
        "(" +
        "id, " +
        "cache_control" +
        ") VALUES(?, ?)";
    private static final String BATCH_DELETE_MAP_SQL =
        "DELETE FROM cache_control_map ccm WHERE ccm.id = ? AND ccm.cache_control IN (%s)";

    private static final String DELETE_SQL = "DELETE FROM cache_profile cp WHERE cp.id = ?";
    private static final String DELETE_MAP_BY_ID_SQL =
        "DELETE FROM cache_control_map ccm WHERE ccm.id = ?";

    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM cache_profile cp LEFT JOIN cache_control_map ccm ON cp.id = ccm.id WHERE cp.domain_id = ?";

    private static final String FIND_BY_DOMAIN_IDS_SQL =
        "SELECT * FROM (SELECT * FROM cache_profile cp WHERE cp.domain_id IN (%s) ORDER BY cp.id ASC LIMIT ? OFFSET ?) AS tcp LEFT JOIN cache_control_map ccm ON tcp.id = ccm.id";
    private static final String COUNT_BY_DOMAIN_IDS_SQL =
        "SELECT COUNT(*) AS count FROM cache_profile cp WHERE cp.domain_id IN (%s)";

    private static final String FIND_BY_PROJECT_ID_SQL =
        "SELECT * FROM (SELECT * FROM cache_profile cp WHERE cp.project_id = ? ORDER BY cp.id ASC LIMIT ? OFFSET ?) AS tcp LEFT JOIN cache_control_map ccm ON tcp.id = ccm.id";
    private static final String COUNT_BY_PROJECT_ID_SQL =
        "SELECT COUNT(*) AS count FROM cache_profile cp WHERE cp.project_id = ?";

    private static final String FIND_BY_PROJECT_ID_AND_DOMAIN_ID_SQL =
        "SELECT * FROM (SELECT * FROM cache_profile cp WHERE cp.project_id = ? AND cp.domain_id = ?) AS tcp LEFT JOIN cache_control_map ccm ON tcp.id = ccm.id";

    @Override
    public CacheProfile query(CacheProfileId id) {
        List<CacheProfile> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                id.getDomainId()
            );
        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public void add(CacheProfile cacheProfile) {
        long milli = Instant.now().toEpochMilli();
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                cacheProfile.getId(),
                milli,
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                milli,
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                0,
                cacheProfile.getAllowCache(),
                cacheProfile.getCacheProfileId().getDomainId(),
                cacheProfile.getDescription(),
                cacheProfile.getEtag(),
                cacheProfile.getExpires(),
                cacheProfile.getMaxAge(),
                cacheProfile.getName(),
                cacheProfile.getSmaxAge(),
                cacheProfile.getVary(),
                cacheProfile.getWeakValidation(),
                cacheProfile.getProjectId().getDomainId()
            );
        if (Checker.notNullOrEmpty(cacheProfile.getCacheControl())) {
            //TODO manually convert batch?
            List<BatchInsertKeyValue> batchArgs = new ArrayList<>();
            cacheProfile.getCacheControl().forEach(e -> {
                batchArgs.add(new BatchInsertKeyValue(cacheProfile.getId(), e.name()));
            });
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_MAP_SQL, batchArgs, batchArgs.size(),
                    (ps, row) -> {
                        ps.setLong(1, row.getId());
                        ps.setString(2, row.getValue());
                    });
        }
    }

    @Override
    public void remove(CacheProfile cacheProfile) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_SQL,
                cacheProfile.getId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_MAP_BY_ID_SQL,
                cacheProfile.getId()
            );
    }

    @Override
    public SumPagedRep<CacheProfile> query(CacheProfileQuery query) {
        if (Checker.notNull(query.getProjectId()) && Checker.notNullOrEmpty(query.getIds())) {
            //tenant query
            return queryByProjectIdAndDomainId(query);
        }
        if (Checker.notNull(query.getProjectId())) {
            //tenant query
            return queryByProjectId(query);
        }
        if (Checker.notNullOrEmpty(query.getIds())) {
            return queryByDomainIds(query);
        }
        return SumPagedRep.empty();
    }

    @Override
    public void update(CacheProfile old, CacheProfile updated) {
        if (updated.equals(old)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                Instant.now().toEpochMilli(),
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                updated.getVersion() + 1,
                updated.getAllowCache(),
                updated.getCacheProfileId().getDomainId(),
                updated.getDescription(),
                updated.getEtag(),
                updated.getExpires(),
                updated.getMaxAge(),
                updated.getName(),
                updated.getSmaxAge(),
                updated.getVary(),
                updated.getWeakValidation(),
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
        DatabaseUtility.updateMap(old.getCacheControl(), updated.getCacheControl(), (added) -> {
            //TODO manually convert batch?
            List<BatchInsertKeyValue> batchArgs = new ArrayList<>();
            added.forEach(e -> {
                batchArgs.add(new BatchInsertKeyValue(updated.getId(), e.name()));
            });
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_MAP_SQL, batchArgs, batchArgs.size(),
                    (ps, row) -> {
                        ps.setLong(1, row.getId());
                        ps.setString(2, row.getValue());
                    });
        }, (removed) -> {
            List<Object> args = new ArrayList<>();
            Set<String> names = removed.stream().map(Enum::name).collect(Collectors.toSet());
            String inSql = DatabaseUtility.getInClause(names.size());
            args.add(updated.getId());
            args.addAll(names);
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_MAP_SQL, inSql),
                    args.toArray()
                );
        });
    }

    private SumPagedRep<CacheProfile> queryByProjectIdAndDomainId(
        CacheProfileQuery cacheProfileQuery) {
        List<CacheProfile> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_PROJECT_ID_AND_DOMAIN_ID_SQL,
                new RowMapper(),
                cacheProfileQuery.getProjectId().getDomainId(),
                cacheProfileQuery.getIds().stream().findFirst().get().getDomainId()
            );
        return new SumPagedRep<>(data, (long) data.size());
    }

    public SumPagedRep<CacheProfile> queryByDomainIds(CacheProfileQuery query) {
        String inSql = DatabaseUtility.getInClause(query.getIds().size());
        List<Object> args = query.getIds().stream().map(DomainId::getDomainId).distinct()
            .collect(Collectors.toList());
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<CacheProfile> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format(FIND_BY_DOMAIN_IDS_SQL, inSql),
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format(COUNT_BY_DOMAIN_IDS_SQL, inSql),
                new DatabaseUtility.ExtractCount(),
                query.getIds().stream().map(DomainId::getDomainId).distinct().toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    public SumPagedRep<CacheProfile> queryByProjectId(CacheProfileQuery query) {
        List<CacheProfile> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_PROJECT_ID_SQL,
                new RowMapper(),
                query.getProjectId().getDomainId(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_BY_PROJECT_ID_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getProjectId().getDomainId());
        return new SumPagedRep<>(data, count);
    }

    private static class RowMapper implements ResultSetExtractor<List<CacheProfile>> {

        @Override
        public List<CacheProfile> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<CacheProfile> cacheProfiles = new ArrayList<>();
            long currentId = -1L;
            CacheProfile cacheProfile = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    cacheProfile = CacheProfile.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        DatabaseUtility.getNullableBoolean(rs, "allow_cache"),
                        new CacheProfileId(rs.getString("domain_id")),
                        rs.getString("description"),
                        DatabaseUtility.getNullableBoolean(rs, "etag"),
                        DatabaseUtility.getNullableLong(rs, "expires"),
                        DatabaseUtility.getNullableLong(rs, "max_age"),
                        rs.getString("name"),
                        DatabaseUtility.getNullableLong(rs, "smax_age"),
                        rs.getString("vary"),
                        DatabaseUtility.getNullableBoolean(rs, "weak_validation"),
                        new ProjectId(rs.getString("project_id"))
                    );
                    cacheProfiles.add(cacheProfile);
                    currentId = dbId;
                }
                Set<CacheControlValue> cacheControl =
                    cacheProfile.getCacheControl();
                String controlValue = rs.getString("cache_control");
                if (Checker.notNull(controlValue)) {
                    CacheControlValue cacheControlValue =
                        CacheControlValue.valueOf(controlValue);
                    cacheControl.add(cacheControlValue);
                }
            } while (rs.next());
            return cacheProfiles;
        }
    }
}
