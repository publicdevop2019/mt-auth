package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginHistoryRepository;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class JdbcLoginHistoryRepository implements LoginHistoryRepository {
    @Override
    public void add(LoginHistory info) {
        CommonDomainRegistry.getJdbcTemplate()
            .update("INSERT INTO login_history " +
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
    public Set<LoginHistory> getLast100Login(UserId userId) {
        Object query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                "SELECT * FROM login_history lh WHERE lh.domain_id = ? ORDER BY lh.login_at DESC LIMIT 100",
                new Object[] {userId.getDomainId()},
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return Collections.emptySet();
                        }
                        Set<LoginHistory> objects = new HashSet<>();
                        do {
                            LoginHistory loginHistory = LoginHistory.create(
                                rs.getLong("id"),
                                new UserId(rs.getString("domain_id")),
                                rs.getLong("login_at"),
                                rs.getString("ip_address"),
                                rs.getString("agent")
                            );
                            objects.add(loginHistory);
                        } while (rs.next());
                        return objects;
                    }
                });
        return (Set<LoginHistory>) query;
    }
}
