package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.LoginInfoRepository;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserLoginRequest;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLoginInfoRepository implements LoginInfoRepository {
    @Override
    public Optional<LoginInfo> query(UserId userId) {
        Object query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                "SELECT * FROM login_info li WHERE li.domain_id = ?",
                new Object[] {userId.getDomainId()},
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return Collections.emptySet();
                        }
                        return LoginInfo.create(
                            rs.getLong("id"),
                            new UserId(rs.getString("domain_id")),
                            rs.getLong("login_at"),
                            rs.getString("ip_address"),
                            rs.getString("agent")
                        );
                    }
                });
        return Optional.ofNullable((LoginInfo) query);
    }

    @Override
    public void add(LoginInfo info) {
        CommonDomainRegistry.getJdbcTemplate()
            .update("INSERT INTO login_info " +
                    "(" +
                    "id, " +
                    "login_at, " +
                    "domain_id, " +
                    "ip_address, " +
                    "agent" +
                    ") VALUES" +
                    "(?,?,?,?,?)",
                info.getId(),
                info.getLoginAt(),
                info.getUserId().getDomainId(),
                info.getIpAddress(),
                info.getAgent()
            );
    }

    @Override
    public void updateLastLogin(UserLoginRequest command, UserId userId) {
        long loginAt = Instant.now().toEpochMilli();
        String ipAddress = command.getIpAddress();
        String agent = command.getAgent();
        String sql =
            "UPDATE login_info li SET li.login_at  = ?, li.ip_address = ?, li.agent = ? WHERE li.domain_id = ?";
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(sql, loginAt, ipAddress, agent, userId.getDomainId());
        DatabaseUtility.checkUpdate(update);
    }
}
