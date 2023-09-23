package com.mt.access.port.adapter.persistence.cross_domain_validation;

import com.mt.access.domain.model.cross_domain_validation.ValidationResult;
import com.mt.access.domain.model.cross_domain_validation.ValidationResultRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcValidationResultRepository implements ValidationResultRepository {

    private static final String INSERT_SQL = "INSERT INTO validation_result " +
        "(" +
        "id, " +
        "failure_count, " +
        "notify_admin" +
        ") VALUES(?, ?, ?)";
    private static final String FIND_ANY_VALIDATION_RESULT_SQL = "SELECT * FROM validation_result";
    private static final String UPDATE_SQL = "UPDATE validation_result vr SET " +
        "vr.failure_count = ? ," +
        "vr.notify_admin = ? " +
        "WHERE vr.id = ?";

    @Override
    public Optional<ValidationResult> query() {
        List<ValidationResult> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ANY_VALIDATION_RESULT_SQL,
                new RowMapper()
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public void create(ValidationResult result) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                result.getId(),
                result.getFailureCount(),
                result.getNotifyAdmin()
            );
    }

    @Override
    public void update(ValidationResult result) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                result.getFailureCount(),
                result.getNotifyAdmin(),
                result.getId()
            );
    }

    private static class RowMapper implements ResultSetExtractor<List<ValidationResult>> {

        @Override
        public List<ValidationResult> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<ValidationResult> results = new ArrayList<>();
            long currentId = -1L;
            ValidationResult result;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    result = ValidationResult.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableInteger(rs, "failure_count"),
                        DatabaseUtility.getNullableBoolean(rs, "notify_admin")
                    );
                    results.add(result);
                    currentId = dbId;
                }
            } while (rs.next());
            return results;
        }
    }
}
