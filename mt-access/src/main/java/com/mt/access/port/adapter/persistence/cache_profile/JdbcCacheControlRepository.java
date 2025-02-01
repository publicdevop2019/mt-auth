package com.mt.access.port.adapter.persistence.cache_profile;

import com.mt.access.domain.model.cache_profile.CacheControlRepository;
import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
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
public class JdbcCacheControlRepository implements CacheControlRepository {
    private static final String BATCH_DELETE_CACHE_CONTROL_MAP_SQL =
        "DELETE FROM cache_control_map ccm WHERE ccm.id = ? AND ccm.cache_control IN (%s)";
    private static final String DELETE_CACHE_CONTROL_MAP_BY_ID_SQL =
        "DELETE FROM cache_control_map ccm WHERE ccm.id = ?";
    private static final String FIND_CACHE_CONTROL_BY_ID_SQL =
        "SELECT cache_control FROM cache_control_map ccm WHERE ccm.id = ?";
    private static final String INSERT_CACHE_CONTROL_MAP_SQL = "INSERT INTO cache_control_map " +
        "(" +
        "id, " +
        "cache_control" +
        ") VALUES(?, ?)";

    @Override
    public Set<CacheControlValue> query(CacheProfile cacheProfile) {
        List<CacheControlValue> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_CACHE_CONTROL_BY_ID_SQL,
                new RowMapper(),
                cacheProfile.getId()
            );
        return new LinkedHashSet<>(data);
    }

    @Override
    public void remove(CacheProfile cacheProfile, Set<CacheControlValue> values) {
        if (Checker.notNullOrEmpty(values)) {
            List<Object> args = new ArrayList<>();
            Set<String> names = Utility.mapToSet(values, Enum::name);
            String inSql = DatabaseUtility.getInClause(names.size());
            args.add(cacheProfile.getId());
            args.addAll(names);
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_CACHE_CONTROL_MAP_SQL, inSql),
                    args.toArray()
                );
        }
    }

    @Override
    public void add(CacheProfile cacheProfile, Set<CacheControlValue> values) {
        if (Checker.notNullOrEmpty(values)) {
            List<BatchInsertKeyValue> batchArgs = Utility.mapToList(values,
                e -> new BatchInsertKeyValue(cacheProfile.getId(), e.name()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_CACHE_CONTROL_MAP_SQL, batchArgs, batchArgs.size(),
                    (ps, row) -> {
                        ps.setLong(1, row.getId());
                        ps.setString(2, row.getValue());
                    });
        }
    }

    @Override
    public void removeAll(CacheProfile cacheProfile, Set<CacheControlValue> values) {
        if (Checker.notNullOrEmpty(values)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_CACHE_CONTROL_MAP_BY_ID_SQL,
                    cacheProfile.getId()
                );
        }
    }

    private static class RowMapper
        implements ResultSetExtractor<List<CacheControlValue>> {

        @Override
        public List<CacheControlValue> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<CacheControlValue> list = new ArrayList<>();
            do {
                String raw = rs.getString("cache_control");
                if (Checker.notNull(raw)) {
                    CacheControlValue cacheControlValue = CacheControlValue.valueOf(raw);
                    list.add(cacheControlValue);
                }
            } while (rs.next());
            return list;
        }
    }
}
