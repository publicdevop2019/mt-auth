package com.mt.common.port.adapter.persistence.idempotent;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.idempotent.ChangeRecord;
import com.mt.common.domain.model.idempotent.ChangeRecordRepository;
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
public class JdbcChangeRecordRepository implements ChangeRecordRepository {

    private static final String FIND_BY_CHANGE_ID_TYPE_SQL =
        "SELECT * FROM change_record cr WHERE cr.change_id = ? AND cr.entity_type = ?";
    private static final String INSERT_SQL = "INSERT INTO change_record " +
        "(" +
        "id, " +
        "change_id, " +
        "entity_type, " +
        "empty_opt, " +
        "return_value " +
        ") VALUES" +
        "(?,?,?,?,?)";

    @Override
    public Optional<ChangeRecord> internalQuery(String changeId, String entityType) {
        List<ChangeRecord> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_CHANGE_ID_TYPE_SQL,
                new RowMapper(),
                changeId,
                entityType
            );
        return data.isEmpty() ? Optional.empty() : Optional.of(data.get(0));
    }

    @Override
    public void add(ChangeRecord changeRecord) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                changeRecord.getId(),
                changeRecord.getChangeId(),
                changeRecord.getEntityType(),
                changeRecord.getEmptyOpt(),
                changeRecord.getReturnValue()
            );
    }

    private static class RowMapper implements ResultSetExtractor<List<ChangeRecord>> {

        @Override
        public List<ChangeRecord> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<ChangeRecord> list = new ArrayList<>();
            long currentId = -1L;
            ChangeRecord changeRecord;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    changeRecord = ChangeRecord.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        rs.getString("change_id"),
                        rs.getString("entity_type"),
                        DatabaseUtility.getNullableBoolean(rs, "empty_opt"),
                        rs.getString("return_value")
                    );
                    list.add(changeRecord);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
