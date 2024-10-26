package com.mt.access.port.adapter.persistence.operation_cool_down;

import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.infrastructure.operation_cool_down.OperationCoolDown;
import com.mt.access.infrastructure.operation_cool_down.OperationCoolDownRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcOperationCoolDownRepository implements OperationCoolDownRepository {

    private static final String FIND_BY_EXECUTOR_OPERATION_TYPE_SQL =
        "SELECT * FROM opt_cool_down AS ocd WHERE ocd.executor = ? AND ocd.opt_type = ?";
    private static final String INSERT_SQL = "INSERT INTO opt_cool_down (" +
        "opt_type, " +
        "executor, " +
        "last_opt_at" +
        ") VALUES(?, ?, ?)";
    private static final String UPDATE_SQL =
        "UPDATE opt_cool_down AS t SET ocd.last_opt_at = ? WHERE ocd.executor = ? " +
            "AND ocd.opt_type = ? AND ocd.last_opt_at = ?";

    @Override
    public Optional<OperationCoolDown> query(String executor, OperationType operationType) {
        List<OperationCoolDown> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_EXECUTOR_OPERATION_TYPE_SQL,
                new RowMapper(),
                executor,
                operationType.name()
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public void add(OperationCoolDown coolDown) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                coolDown.getOperationType().name(),
                coolDown.getExecutor(),
                coolDown.getLastOperateAt()
            );
    }

    @Override
    public void updateLastOperateAt(OperationCoolDown coolDown) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                Instant.now().toEpochMilli(),
                coolDown.getExecutor(),
                coolDown.getOperationType().name(),
                coolDown.getLastOperateAt()
            );
        DatabaseUtility.checkUpdate(update);
    }

    private static class RowMapper implements ResultSetExtractor<List<OperationCoolDown>> {

        @Override
        public List<OperationCoolDown> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<OperationCoolDown> results = new ArrayList<>();
            String currentId = "";
            OperationCoolDown result;
            do {
                String dbId = rs.getString("executor") + rs.getString("opt_type");
                if (!currentId.equals(dbId)) {
                    result = OperationCoolDown.fromDatabaseRow(
                        OperationType.valueOf(rs.getString("opt_type")),
                        rs.getString("executor"),
                        DatabaseUtility.getNullableLong(rs, "last_opt_at")
                    );
                    results.add(result);
                    currentId = dbId;
                }
            } while (rs.next());
            return results;
        }
    }
}
