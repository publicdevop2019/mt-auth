package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.report.RawAccessRecord;
import com.mt.access.domain.model.report.RawAccessRecordRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRawAccessRecordRepository implements RawAccessRecordRepository {


    private static final String INSERT_SQL = "INSERT INTO raw_access_record " +
        "(" +
        "id, " +
        "name, " +
        "instance_id, " +
        "record_id, " +
        "record, " +
        "is_request, " +
        "processed, " +
        "is_response, " +
        "uuid" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?)";
    private static final String FIND_BY_UUID_SQL = "SELECT * FROM raw_access_record rar " +
        "WHERE rar.uuid in (%s) AND rar.is_response = 1";
    private static final String FIND_BUCKET_SQL = "SELECT * FROM raw_access_record rar " +
        "WHERE rar.id > ? AND rar.is_request = 1 ORDER BY rar.id ASC LIMIT 100";
    private static final String UPDATE_SQL = "UPDATE raw_access_record rar " +
        "SET rar.processed = 1 WHERE rar.id IN (%s)";

    @Override
    public Set<RawAccessRecord> getBucketRequestRecordSinceId(Long id) {
        List<RawAccessRecord> query = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BUCKET_SQL,
                new RowMapper(),
                id
            );
        return new HashSet<>(query);
    }

    @Override
    public Set<RawAccessRecord> getResponseForUuid(Set<String> uuids) {
        String inClause = DatabaseUtility.getInClause(uuids.size());
        List<RawAccessRecord> query = CommonDomainRegistry.getJdbcTemplate()
            .query(String.format(FIND_BY_UUID_SQL, inClause),
                new RowMapper(),
                uuids.toArray()
            );
        return new HashSet<>(query);
    }

    @Override
    public void addAll(Set<RawAccessRecord> records) {
        List<RawAccessRecord> arrayList = new ArrayList<>(records);
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate(INSERT_SQL, arrayList, records.size(),
                (ps, record) -> {
                    ps.setLong(1, record.getId());
                    ps.setString(2, record.getName());
                    ps.setString(3, record.getInstanceId());
                    ps.setString(4, record.getRecordId());
                    ps.setString(5, record.getRecord());
                    ps.setBoolean(6, record.getIsRequest());
                    ps.setBoolean(7, record.getProcessed());
                    ps.setBoolean(8, record.getIsResponse());
                    ps.setString(9, record.getUuid());
                });
    }

    @Override
    public void updateAllToProcessed(Set<RawAccessRecord> foundedRecords) {
        String inClause = DatabaseUtility.getInClause(foundedRecords.size());
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(String.format(UPDATE_SQL, inClause),
                foundedRecords.stream().map(RawAccessRecord::getId).distinct().toArray()
            );
        DatabaseUtility.checkUpdate(update, foundedRecords.size());
    }

    private static class RowMapper implements ResultSetExtractor<List<RawAccessRecord>> {

        @Override
        public List<RawAccessRecord> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<RawAccessRecord> records = new ArrayList<>();
            RawAccessRecord record;
            do {
                record = RawAccessRecord.fromDatabaseRow(
                    DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                    rs.getString("name"),
                    rs.getString("instance_id"),
                    rs.getString("record_id"),
                    rs.getString("record"),
                    DatabaseUtility.getNullableBoolean(rs, "is_request"),
                    DatabaseUtility.getNullableBoolean(rs, "processed"),
                    DatabaseUtility.getNullableBoolean(rs, "is_response"),
                    rs.getString("uuid")
                );
                records.add(record);
            } while (rs.next());
            return records;
        }
    }
}
