package com.mt.access.port.adapter.persistence.verification_code;

import com.mt.access.domain.model.activation_code.Code;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.access.domain.model.verification_code.VerificationCode;
import com.mt.access.domain.model.verification_code.VerificationCodeRepository;
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
public class JdbcVerificationCodeRepository implements VerificationCodeRepository {

    private static final String INSERT_SQL = "INSERT INTO verification_code (" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "code, " +
        "domain_id" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM verification_code t WHERE t.domain_id = ?";
    private static final String UPDATE_CODE_SQL =
        "UPDATE verification_code t " +
            "SET t.code = ?, t.modified_at = ?, t.modified_by = ? " +
            "WHERE t.domain_id = ?";

    @Override
    public Optional<VerificationCode> query(RegistrationEmail email) {
        List<VerificationCode> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                email.getDomainId()
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public void add(ClientId clientId, VerificationCode verificationCode) {
        long milli = Instant.now().toEpochMilli();
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                verificationCode.getId(),
                milli,
                clientId.getDomainId(),
                milli,
                clientId.getDomainId(),
                0,
                verificationCode.getCode().getValue(),
                verificationCode.getDomainId().getDomainId()
            );
    }

    @Override
    public void updateCode(ClientId clientId, RegistrationEmail email,
                           Code code) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_CODE_SQL,
                code.getValue(),
                Instant.now().toEpochMilli(),
                clientId.getDomainId(),
                email.getDomainId()
            );
        DatabaseUtility.checkUpdate(update);
    }

    @Override
    public void updateCode(ClientId clientId, RegistrationMobile mobile,
                           Code code) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_CODE_SQL,
                code.getValue(),
                Instant.now().toEpochMilli(),
                clientId.getDomainId(),
                mobile.getDomainId()
            );
        DatabaseUtility.checkUpdate(update);
    }

    @Override
    public Optional<VerificationCode> query(RegistrationMobile userMobile) {
        List<VerificationCode> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                userMobile.getDomainId()
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    private static class RowMapper implements ResultSetExtractor<List<VerificationCode>> {

        @Override
        public List<VerificationCode> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<VerificationCode> results = new ArrayList<>();
            long currentId = -1L;
            VerificationCode result;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    result = VerificationCode.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        new Code(rs.getString("code")),
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
