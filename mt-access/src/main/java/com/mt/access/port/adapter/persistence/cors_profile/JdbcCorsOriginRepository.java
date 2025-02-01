package com.mt.access.port.adapter.persistence.cors_profile;

import com.mt.access.domain.model.cors_profile.CorsOriginRepository;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.infrastructure.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCorsOriginRepository implements CorsOriginRepository {
    private static final String DELETE_CORS_ORIGIN_BY_ID_SQL =
        "DELETE FROM cors_origin_map com WHERE com.id = ?";
    private static final String FIND_ALLOWED_ORIGIN_BY_ID_SQL =
        "SELECT * FROM cors_origin_map t WHERE t.id = ?";
    private static final String INSERT_CORS_ORIGIN_MAP_SQL = "INSERT INTO cors_origin_map " +
        "(" +
        "id, " +
        "allowed_origin" +
        ") VALUES(?, ?)";
    private static final String BATCH_DELETE_CORS_ORIGIN_MAP_SQL =
        "DELETE FROM cors_origin_map com WHERE com.id = ? AND com.allowed_origin IN (%s)";

    @Override
    public Set<Origin> query(CorsProfile corsProfile) {
        List<Origin> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALLOWED_ORIGIN_BY_ID_SQL,
                new RowMapper(),
                corsProfile.getId()
            );
        return new LinkedHashSet<>(data);
    }

    @Override
    public void remove(CorsProfile corsProfile, Set<Origin> origins) {
        if (Checker.notNullOrEmpty(origins)) {
            List<Object> args = new ArrayList<>();
            String inSql = DatabaseUtility.getInClause(origins.size());
            args.add(corsProfile.getId());
            args.addAll(Utility.mapToSet(origins, Origin::getValue));
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_CORS_ORIGIN_MAP_SQL, inSql),
                    args.toArray()
                );
        }
    }

    @Override
    public void add(CorsProfile corsProfile, Set<Origin> origins) {
        if (Checker.notNullOrEmpty(origins)) {
            List<BatchInsertKeyValue> batchArgs = Utility.mapToList(origins,
                e -> new BatchInsertKeyValue(corsProfile.getId(), e.getValue()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_CORS_ORIGIN_MAP_SQL, batchArgs, batchArgs.size(),
                    (ps, row) -> {
                        ps.setLong(1, row.getId());
                        ps.setString(2, row.getValue());
                    });
        }
    }

    @Override
    public void removeAll(CorsProfile corsProfile, Set<Origin> origins) {
        if (Checker.notNullOrEmpty(origins)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_CORS_ORIGIN_BY_ID_SQL,
                    corsProfile.getId()
                );
        }
    }

    private static class RowMapper implements ResultSetExtractor<List<Origin>> {

        @Override
        public List<Origin> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Origin> list = new ArrayList<>();
            do {
                String raw = rs.getString("allowed_origin");
                if (Checker.notNull(raw)) {
                    list.add(new Origin(raw));
                }
            } while (rs.next());
            return list;
        }
    }
}
