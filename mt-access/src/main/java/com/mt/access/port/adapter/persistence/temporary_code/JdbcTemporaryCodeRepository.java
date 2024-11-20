package com.mt.access.port.adapter.persistence.temporary_code;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.temporary_code.TemporaryCode;
import com.mt.access.domain.model.temporary_code.TemporaryCodeRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.AnyDomainId;
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
public class JdbcTemporaryCodeRepository implements TemporaryCodeRepository {

    private static final String INSERT_SQL = "INSERT INTO temporary_code (" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "code, " +
        "operation_type, " +
        "domain_id" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_NONE_EXPIRED_SQL =
        "SELECT * FROM temporary_code t WHERE t.domain_id = ? AND t.operation_type = ? AND t.modified_at >= ?";
    private static final String FIND_SQL =
        "SELECT * FROM temporary_code t WHERE t.domain_id = ? AND t.operation_type = ?";
    private static final String UPDATE_CODE_SQL =
        "UPDATE temporary_code t " +
            "SET t.code = ?, t.modified_at = ?, t.modified_by = ? " +
            "WHERE t.domain_id = ? AND t.operation_type = ?";
    private static final String DELETE_CODE_SQL =
        "DELETE FROM temporary_code t " +
            "WHERE t.domain_id = ? AND t.operation_type = ?";

    @Override
    public Optional<TemporaryCode> query(String operationType, AnyDomainId domainId) {
        List<TemporaryCode> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_SQL,
                new RowMapper(),
                domainId.getDomainId(),
                operationType
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public Optional<TemporaryCode> queryNoneExpired(String operationType, AnyDomainId domainId,
                                                    Integer expireInMilli) {
        List<TemporaryCode> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_NONE_EXPIRED_SQL,
                new RowMapper(),
                domainId.getDomainId(),
                operationType,
                Instant.now().toEpochMilli() - expireInMilli
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public void add(ClientId clientId, TemporaryCode temporaryCode) {
        long milli = Instant.now().toEpochMilli();
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                temporaryCode.getId(),
                milli,
                clientId.getDomainId(),
                milli,
                clientId.getDomainId(),
                0,
                temporaryCode.getCode(),
                temporaryCode.getOperationType(),
                temporaryCode.getDomainId().getDomainId()
            );
    }

    @Override
    public void updateCode(ClientId clientId, AnyDomainId domainId, String code,
                           String operationType) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_CODE_SQL,
                code,
                Instant.now().toEpochMilli(),
                clientId.getDomainId(),
                domainId.getDomainId(),
                operationType
            );
        DatabaseUtility.checkUpdate(update);
    }

    @Override
    public void consume(String operationType, AnyDomainId domainId) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_CODE_SQL,
                domainId.getDomainId(),
                operationType
            );
        DatabaseUtility.checkUpdate(update);
    }

    private static class RowMapper implements ResultSetExtractor<List<TemporaryCode>> {

        @Override
        public List<TemporaryCode> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<TemporaryCode> results = new ArrayList<>();
            long currentId = -1L;
            TemporaryCode result;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    result = TemporaryCode.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        rs.getString("code"),
                        new AnyDomainId(rs.getString("domain_id"))
                    );
                    results.add(result);
                    currentId = dbId;
                }
            } while (rs.next());
            return results;
        }
    }
}
