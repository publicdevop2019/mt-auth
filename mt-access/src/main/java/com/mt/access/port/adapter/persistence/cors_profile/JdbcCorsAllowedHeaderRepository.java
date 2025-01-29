package com.mt.access.port.adapter.persistence.cors_profile;

import com.mt.access.domain.model.cors_profile.CorsAllowedHeaderRepository;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
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
public class JdbcCorsAllowedHeaderRepository implements CorsAllowedHeaderRepository {
    private static final String FIND_ALLOWED_HEADER_BY_ID_SQL =
        "SELECT * FROM allowed_header_map t WHERE t.id = ?";
    private static final String INSERT_ALLOWED_HEADER_MAP_SQL = "INSERT INTO allowed_header_map " +
        "(" +
        "id, " +
        "allowed_header" +
        ") VALUES(?, ?)";
    private static final String BATCH_DELETE_ALLOWED_HEADER_MAP_SQL =
        "DELETE FROM allowed_header_map ahm WHERE ahm.id = ? AND ahm.allowed_header IN (%s)";
    private static final String DELETE_ALLOWED_HEADER_BY_ID_SQL =
        "DELETE FROM allowed_header_map ahm WHERE ahm.id = ?";

    @Override
    public Set<String> query(CorsProfile corsProfile) {
        List<String> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_ALLOWED_HEADER_BY_ID_SQL,
                new RowMapper(),
                corsProfile.getId()
            );
        return new LinkedHashSet<>(data);
    }

    @Override
    public void remove(CorsProfile corsProfile, Set<String> headers) {
        if (Utility.notNullOrEmpty(headers)) {
            List<Object> args = new ArrayList<>();
            String inSql = DatabaseUtility.getInClause(headers.size());
            args.add(corsProfile.getId());
            args.addAll(headers);
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_ALLOWED_HEADER_MAP_SQL, inSql),
                    args.toArray()
                );
        }
    }

    @Override
    public void add(CorsProfile corsProfile, Set<String> headers) {
        if (Utility.notNullOrEmpty(headers)) {
            List<BatchInsertKeyValue> batchArgs = Utility.mapToList(headers,
                e -> new BatchInsertKeyValue(corsProfile.getId(), e));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_ALLOWED_HEADER_MAP_SQL, batchArgs, batchArgs.size(),
                    (ps, row) -> {
                        ps.setLong(1, row.getId());
                        ps.setString(2, row.getValue());
                    });
        }
    }

    @Override
    public void removeAll(CorsProfile corsProfile, Set<String> headers) {
        if (Utility.notNullOrEmpty(headers)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_ALLOWED_HEADER_BY_ID_SQL, corsProfile.getId());
        }
    }

    private static class RowMapper implements ResultSetExtractor<List<String>> {

        @Override
        public List<String> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<String> list = new ArrayList<>();
            do {
                String raw = rs.getString("allowed_header");
                if (Utility.notNull(raw)) {
                    list.add(raw);
                }
            } while (rs.next());
            return list;
        }
    }
}
