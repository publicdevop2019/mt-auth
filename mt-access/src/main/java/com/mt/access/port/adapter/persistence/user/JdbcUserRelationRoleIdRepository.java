package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationRoleIdRepository;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.infrastructure.Utility;
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
public class JdbcUserRelationRoleIdRepository implements UserRelationRoleIdRepository {
    private static final String INSERT_ROLE_MAP = "INSERT INTO user_relation_role_map " +
        "(" +
        "id, " +
        "role" +
        ") VALUES " +
        "(?,?)";
    private static final String FIND_USER_RELATION_ROLE_ID_BY_ID_SQL =
        "SELECT * FROM user_relation_role_map t WHERE t.id = ?";
    private static final String BATCH_DELETE_ROLE_MAP_BY_ROLE_SQL =
        "DELETE FROM user_relation_role_map urrm WHERE urrm.id = ? AND urrm.role IN (%s)";

    @Override
    public Set<RoleId> query(UserRelation userRelation) {
        List<RoleId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_USER_RELATION_ROLE_ID_BY_ID_SQL,
                new RowMapper(),
                userRelation.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void add(UserRelation userRelation, Set<RoleId> roleIds) {
        if (Checker.notNullOrEmpty(roleIds)) {
            Set<BatchInsertKeyValue> args = Utility.mapToSet(roleIds,
                e -> new BatchInsertKeyValue(userRelation.getId(), e.getDomainId()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_ROLE_MAP, args,
                    args.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void remove(UserRelation userRelation, RoleId roleId) {
        if (Checker.notNull(roleId)) {
            List<Object> args = new ArrayList<>();
            String inSql = DatabaseUtility.getInClause(1);
            args.add(userRelation.getId());
            args.add(roleId.getDomainId());
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_ROLE_MAP_BY_ROLE_SQL, inSql),
                    args.toArray()
                );
        }
    }

    private static class RowMapper implements ResultSetExtractor<List<RoleId>> {

        @Override
        public List<RoleId> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<RoleId> list = new ArrayList<>();
            do {
                String idRaw = rs.getString("role");
                if (Checker.notNull(idRaw)) {
                    list.add(new RoleId(idRaw));
                }
            } while (rs.next());
            return list;
        }
    }
}
