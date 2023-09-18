package com.mt.access.port.adapter.persistence.pending_user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.PendingUserRepository;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
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
public class JdbcPendingUserRepository implements PendingUserRepository {

    private static final String INSERT_SQL = "INSERT INTO pending_user (" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "activation_code, " +
        "domain_id" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_EMAIL_SQL =
        "SELECT * FROM pending_user pu WHERE pu.domain_id = ?";
    private static final String UPDATE_ACTIVATION_CODE_SQL =
        "UPDATE pending_user pu " +
            "SET pu.activation_code = ?, pu.modified_at = ?, pu.modified_by = ? " +
            "WHERE pu.domain_id = ?";

    @Override
    public Optional<PendingUser> query(RegistrationEmail email) {
        List<PendingUser> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_EMAIL_SQL,
                new RowMapper(),
                email.getDomainId()
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public void add(ClientId clientId, PendingUser pendingUser) {
        long milli = Instant.now().toEpochMilli();
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                pendingUser.getId(),
                milli,
                clientId.getDomainId(),
                milli,
                clientId.getDomainId(),
                0,
                pendingUser.getActivationCode().getActivationCode(),
                pendingUser.getRegistrationEmail().getDomainId()
            );
    }

    @Override
    public void updateActivationCode(ClientId clientId, RegistrationEmail email, ActivationCode activationCode) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_ACTIVATION_CODE_SQL,
                activationCode.getActivationCode(),
                Instant.now().toEpochMilli(),
                clientId.getDomainId(),
                email.getDomainId()
            );
        DatabaseUtility.checkUpdate(update);
    }

    private static class RowMapper implements ResultSetExtractor<List<PendingUser>> {

        @Override
        public List<PendingUser> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<PendingUser> results = new ArrayList<>();
            long currentId = -1L;
            PendingUser result;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    result = PendingUser.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        new ActivationCode(rs.getString("activation_code")),
                        new RegistrationEmail(rs.getString("domain_id"))
                    );
                    results.add(result);
                    currentId = dbId;
                }
            } while (rs.next());
            return results;
        }
    }
}
