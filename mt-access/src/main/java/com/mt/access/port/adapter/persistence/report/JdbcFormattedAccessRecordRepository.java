package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.report.FormattedAccessRecord;
import com.mt.access.domain.model.report.FormattedAccessRecordRepository;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
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
public class JdbcFormattedAccessRecordRepository implements FormattedAccessRecordRepository {

    private static final String INSERT_RECORD_SQL = "INSERT INTO formatted_access_record " +
        "(" +
        "id, " +
        "endpoint_id, " +
        "request_at, " +
        "path, " +
        "client_ip, " +
        "user_id, " +
        "project_id, " +
        "method, " +
        "response_at, " +
        "response_code, " +
        "response_content_size" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String FIND_SINCE_SQL = "SELECT * FROM formatted_access_record far " +
        "WHERE far.endpoint_id = ? AND far.request_at >= ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM formatted_access_record far " +
        "WHERE far.endpoint_id = ?";


    @Override
    public Set<FormattedAccessRecord> getRecordSince(EndpointId endpointId, long from) {
        List<FormattedAccessRecord> query = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_SINCE_SQL,
                new RowMapper(),
                endpointId.getDomainId(),
                from
            );
        return new HashSet<>(query);
    }

    @Override
    public Set<FormattedAccessRecord> getAllRecord(EndpointId endpointId) {
        List<FormattedAccessRecord> query = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_ALL_SQL,
                new RowMapper(),
                endpointId.getDomainId()
            );
        return new HashSet<>(query);
    }

    @Override
    public void addAll(List<FormattedAccessRecord> records) {
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate(INSERT_RECORD_SQL, records, records.size(),
                (ps, record) -> {
                    ps.setLong(1, record.getId());
                    ps.setString(2, record.getEndpointId().getDomainId());
                    ps.setLong(3, record.getRequestAt());
                    ps.setString(4, record.getPath());
                    ps.setString(5, record.getClientIp());
                    ps.setString(6,
                        record.getUserId() == null ? null : record.getUserId().getDomainId());
                    ps.setString(7,
                        record.getProjectId() == null ? null : record.getProjectId().getDomainId());
                    ps.setString(8, record.getMethod());
                    ps.setLong(9, record.getResponseAt());
                    ps.setInt(10, record.getResponseCode());
                    ps.setInt(11, record.getResponseContentSize());
                });
    }

    private static class RowMapper implements ResultSetExtractor<List<FormattedAccessRecord>> {

        @Override
        public List<FormattedAccessRecord> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<FormattedAccessRecord> records = new ArrayList<>();
            FormattedAccessRecord record;
            do {
                record = FormattedAccessRecord.fromDatabaseRow(
                    DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                    new EndpointId(rs.getString("endpoint_id")),
                    DatabaseUtility.getNullableLong(rs, "request_at"),
                    rs.getString("path"),
                    rs.getString("client_ip"),
                    Utility.notNull(rs.getString("user_id")) ?
                        new UserId(rs.getString("user_id")) : null,
                    Utility.notNull(rs.getString("project_id")) ?
                        new ProjectId(rs.getString("project_id")) : null,
                    rs.getString("method"),
                    DatabaseUtility.getNullableLong(rs, "response_at"),
                    DatabaseUtility.getNullableInteger(rs, "response_code"),
                    DatabaseUtility.getNullableInteger(rs, "response_content_size")
                );
                records.add(record);
            } while (rs.next());
            return records;
        }
    }
}
