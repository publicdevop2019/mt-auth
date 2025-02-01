package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationTenantIdRepository;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRelationTenantIdRepository implements UserRelationTenantIdRepository {

    private static final String INSERT_TENANT_MAP = "INSERT INTO user_relation_tenant_map " +
        "(" +
        "id, " +
        "tenant" +
        ") VALUES " +
        "(?,?)";

    private static final String FIND_USER_RELATION_TENANT_ID_BY_ID_SQL =
        "SELECT * FROM user_relation_tenant_map t WHERE t.id = ?";

    private static final String BATCH_DELETE_TENANT_MAP_BY_TENANT_SQL =
        "DELETE FROM user_relation_tenant_map urtm WHERE urtm.id = ? AND urtm.tenant IN (%s)";
    private static final String DELETE_TENANT_BY_ID_SQL =
        "DELETE FROM user_relation_tenant_map urtm WHERE urtm.id = ?";

    @Override
    public Set<ProjectId> query(UserRelation userRelation) {
        List<ProjectId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_USER_RELATION_TENANT_ID_BY_ID_SQL,
                new RowMapper(),
                userRelation.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void add(UserRelation userRelation, ProjectId projectId) {
        if (Utility.notNull(projectId)) {
            Set<BatchInsertKeyValue> args = new LinkedHashSet<>();
            args.add(new BatchInsertKeyValue(userRelation.getId(), projectId.getDomainId()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_TENANT_MAP, args,
                    args.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void removeAll(UserRelation userRelation, Set<ProjectId> projectIds) {
        if (Utility.notNullOrEmpty(projectIds)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_TENANT_BY_ID_SQL,
                    userRelation.getId()
                );
        }
    }

    @Override
    public void remove(UserRelation userRelation, ProjectId projectId) {
        if (Utility.notNull(projectId)) {
            List<Object> args = new ArrayList<>();
            String inSql = DatabaseUtility.getInClause(1);
            args.add(userRelation.getId());
            args.add(projectId.getDomainId());
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_TENANT_MAP_BY_TENANT_SQL, inSql),
                    args.toArray()
                );
        }
    }

    private static class RowMapper implements ResultSetExtractor<List<ProjectId>> {

        @Override
        public List<ProjectId> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<ProjectId> list = new ArrayList<>();
            do {
                String idRaw = rs.getString("tenant");
                if (Utility.notNull(idRaw)) {
                    list.add(new ProjectId(idRaw));
                }
            } while (rs.next());
            return list;
        }
    }
}
