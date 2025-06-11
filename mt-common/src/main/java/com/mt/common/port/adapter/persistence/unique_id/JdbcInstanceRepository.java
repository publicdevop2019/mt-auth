package com.mt.common.port.adapter.persistence.unique_id;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.instance.Instance;
import com.mt.common.domain.model.instance.InstanceRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcInstanceRepository implements InstanceRepository {
    private static final String INSERT_SQL = "INSERT INTO instance " +
        "(" +
        "id, " +
        "created_at, " +
        "renewed_at, " +
        "url, " +
        "name" +
        ") VALUES " +
        "(?,?,?,?,?)";

    private static final String RENEW_SQL = "UPDATE instance t SET t.renewed_at = ? WHERE t.id = ?";
    private static final String FIND_ALL = "SELECT * FROM instance t";

    private static final String DELETE_SQL = "DELETE FROM instance t WHERE t.id = ?";

    @Override
    public void addInstance(Instance instance) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                instance.getId(),
                instance.getCreatedAt(),
                instance.getRenewedAt(),
                instance.getUrl(),
                instance.getName()
            );
    }

    @Override
    public void removeInstance(Instance instance) {
        CommonDomainRegistry.getJdbcTemplate().update(DELETE_SQL, instance.getId());
    }

    @Override
    public void renewInstance(Instance instance) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(RENEW_SQL,
                instance.getRenewedAt(),
                instance.getId()
            );
    }

    @Override
    public List<Instance> getAllInstances() {
        List<Object> args = new ArrayList<>();
        return CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_ALL,
                new RowMapper(),
                args.toArray()
            );
    }


    private static class RowMapper implements ResultSetExtractor<List<Instance>> {

        @Override
        public List<Instance> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Instance> list = new ArrayList<>();
            long currentId = -1L;
            Instance detail;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    detail = Instance.fromDatabaseRow(
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        DatabaseUtility.getNullableLong(rs, "renewed_at"),
                        rs.getString("url"),
                        rs.getString("name")
                    );
                    list.add(detail);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
