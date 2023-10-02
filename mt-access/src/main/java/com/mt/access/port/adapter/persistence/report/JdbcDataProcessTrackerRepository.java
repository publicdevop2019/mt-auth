package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.report.DataProcessTracker;
import com.mt.access.domain.model.report.DataProcessTrackerRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class JdbcDataProcessTrackerRepository implements DataProcessTrackerRepository {

    private static final String FIND_SQL = "SELECT * FROM data_process_tracker dpt LIMIT 1";
    private static final String INSERT_SQL = "INSERT INTO data_process_tracker " +
        "(" +
        "id, " +
        "last_processed_id, " +
        "version" +
        ") VALUES " +
        "(?,?,?)";
    private static final String UPDATE_SQL = "UPDATE data_process_tracker dpt " +
        "SET dpt.last_processed_id = ?, dpt.version = ? WHERE dpt.version = ? ";

    @Override
    public Optional<DataProcessTracker> get() {
        return CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_SQL,
                new RowMapper()
            );
    }

    @Override
    public void update(DataProcessTracker tracker) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                tracker.getLastProcessedId(),
                tracker.getVersion() + 1,
                tracker.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
    }

    @Override
    public void add(DataProcessTracker tracker) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                tracker.getId(),
                tracker.getLastProcessedId(),
                0
            );
    }

    private static class RowMapper implements ResultSetExtractor<Optional<DataProcessTracker>> {

        @Override
        public Optional<DataProcessTracker> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Optional.empty();
            }
            DataProcessTracker tracker = DataProcessTracker.fromDatabaseRow(
                DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                DatabaseUtility.getNullableLong(rs, "last_processed_id"),
                DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION)
            );
            return Optional.of(tracker);
        }
    }
}
