package com.mt.access.port.adapter.persistence.cors_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.CorsProfileRepository;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCorsProfileRepository implements CorsProfileRepository {
    private static final String FIND_BY_PROJECT_ID_AND_DOMAIN_ID_SQL =
        "SELECT * FROM cors_profile cp WHERE cp.project_id = ? AND cp.domain_id = ?";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM cors_profile cp WHERE cp.domain_id = ?";
    private static final String FIND_BY_DOMAIN_IDS_SQL =
        "SELECT * FROM cors_profile cp WHERE cp.domain_id IN (%s) ORDER BY cp.id ASC LIMIT ? OFFSET ?";

    private static final String COUNT_BY_DOMAIN_IDS_SQL =
        "SELECT COUNT(*) AS count FROM cors_profile cp " +
            "WHERE cp.domain_id IN (%s)";
    private static final String FIND_BY_PROJECT_ID_SQL =
        "SELECT * FROM cors_profile cp WHERE cp.project_id = ? ORDER BY cp.id ASC LIMIT ? OFFSET ?";
    private static final String COUNT_BY_PROJECT_ID_SQL =
        "SELECT COUNT(*) AS count FROM cors_profile cp WHERE cp.project_id = ?";
    private static final String INSERT_SQL = "INSERT INTO cors_profile " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "allow_credentials, " +
        "domain_id, " +
        "description, " +
        "max_age, " +
        "name, " +
        "project_id" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM cors_profile cp WHERE cp.id = ?";

    private static final String UPDATE_SQL = "UPDATE cors_profile cp SET " +
        "cp.modified_at = ? ," +
        "cp.modified_by = ?, " +
        "cp.version = ?, " +
        "cp.allow_credentials = ?, " +
        "cp.description = ?, " +
        "cp.max_age = ?, " +
        "cp.name = ? " +
        "WHERE cp.id = ? AND cp.version = ? ";

    @Override
    public CorsProfile query(CorsProfileId id) {
        List<CorsProfile> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                id.getDomainId()
            );
        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public void add(CorsProfile corsProfile) {
        long milli = Instant.now().toEpochMilli();
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                corsProfile.getId(),
                milli,
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                milli,
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                0,
                corsProfile.getAllowCredentials(),
                corsProfile.getCorsId().getDomainId(),
                corsProfile.getDescription(),
                corsProfile.getMaxAge(),
                corsProfile.getName(),
                corsProfile.getProjectId().getDomainId()
            );
    }

    @Override
    public void update(CorsProfile old, CorsProfile updated) {
        if (updated.equals(old)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                Instant.now().toEpochMilli(),
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                updated.getVersion() + 1,
                updated.getAllowCredentials(),
                updated.getDescription(),
                updated.getMaxAge(),
                updated.getName(),
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
    }

    @Override
    public void remove(CorsProfile corsProfile) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_BY_ID_SQL,
                corsProfile.getId()
            );
    }

    @Override
    public SumPagedRep<CorsProfile> query(CorsProfileQuery query) {
        if (Utility.notNull(query.getProjectId())) {
            //tenant query
            if (Utility.notNullOrEmpty(query.getIds())) {
                return queryByProjectIdAndDomainId(query);
            }
            return queryByProjectId(query);
        } else {
            return queryByDomainIds(query);
        }
    }

    private SumPagedRep<CorsProfile> queryByDomainIds(CorsProfileQuery query) {
        String inSql = DatabaseUtility.getInClause(query.getIds().size());
        List<Object> args = query.getIds().stream().map(DomainId::getDomainId).distinct()
            .collect(Collectors.toList());
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<CorsProfile> data = CommonDomainRegistry.getJdbcTemplate()
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

    private SumPagedRep<CorsProfile> queryByProjectId(CorsProfileQuery query) {
        List<CorsProfile> data = CommonDomainRegistry.getJdbcTemplate()
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

    private SumPagedRep<CorsProfile> queryByProjectIdAndDomainId(
        CorsProfileQuery query) {
        List<CorsProfile> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_PROJECT_ID_AND_DOMAIN_ID_SQL,
                new RowMapper(),
                query.getProjectId().getDomainId(),
                query.getIds().stream().findFirst().get().getDomainId()
            );
        return new SumPagedRep<>(data, (long) data.size());
    }

    private static class RowMapper implements ResultSetExtractor<List<CorsProfile>> {

        @Override
        public List<CorsProfile> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<CorsProfile> corsProfiles = new ArrayList<>();
            long currentId = -1L;
            CorsProfile corsProfile = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    corsProfile = CorsProfile.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        DatabaseUtility.getNullableBoolean(rs, "allow_credentials"),
                        new CorsProfileId(rs.getString("domain_id")),
                        rs.getString("description"),
                        DatabaseUtility.getNullableLong(rs, "max_age"),
                        rs.getString("name"),
                        new ProjectId(rs.getString("project_id"))
                    );
                    corsProfiles.add(corsProfile);
                    currentId = dbId;
                }
            } while (rs.next());
            return corsProfiles;
        }
    }
}
