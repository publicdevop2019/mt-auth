package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginHistoryRepository;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.ResultSetExtractor;

public interface SpringDataJpaLoginHistoryRepository extends JpaRepository<LoginHistory, Long>,
    LoginHistoryRepository {
    default void add(LoginHistory info) {
        save(info);
    }

    default Set<LoginHistory> getLast100Login(UserId userId) {
        Object query = CommonDomainRegistry.getJdbcTemplate()
            .query("SELECT * FROM login_history lh WHERE lh.domain_id = ? ORDER BY lh.login_at DESC LIMIT 100",
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
        return (Set<LoginHistory>)query;
    }
}
