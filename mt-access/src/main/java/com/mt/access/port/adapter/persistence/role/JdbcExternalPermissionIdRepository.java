package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.ExternalPermissionIdRepository;
import com.mt.access.domain.model.role.Role;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.infrastructure.Utility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcExternalPermissionIdRepository implements ExternalPermissionIdRepository {

    private static final String INSERT_EXT_MAP = "INSERT INTO role_external_permission_map " +
        "(" +
        "id, " +
        "permission" +
        ") VALUES " +
        "(?,?)";

    private static final String DELETE_EXT_SQL =
        "DELETE FROM role_external_permission_map map WHERE map.id = ?";
    private static final String DELETE_EXT_MAP_PERMISSION_SQL =
        "DELETE FROM role_external_permission_map map WHERE map.permission = ?";

    private static final String FIND_EXT_PERMISSION = "SELECT m.permission " +
        "FROM role_external_permission_map m WHERE m.id = ?";
    private static final String BATCH_DELETE_EXT_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM role_external_permission_map repm WHERE repm.id = ? AND repm.permission IN (%s)";

    @Override
    public Set<PermissionId> query(Role role) {
        List<PermissionId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_EXT_PERMISSION,
                new PermissionIdRowMapper(),
                role.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void add(Role role, Set<PermissionId> permissionIds) {
        if (Checker.notNullOrEmpty(permissionIds)) {
            Set<BatchInsertKeyValue> args = Utility.mapToSet(permissionIds,
                ee -> new BatchInsertKeyValue(role.getId(), ee.getDomainId()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_EXT_MAP, args,
                    args.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void removeAll(Role role, Set<PermissionId> permissionIds) {
        if (Checker.notNullOrEmpty(permissionIds)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_EXT_SQL,
                    role.getId()
                );
        }
    }

    @Override
    public void remove(Role role, Set<PermissionId> permissionIds) {
        if (Checker.notNullOrEmpty(permissionIds)) {
            String inClause = DatabaseUtility.getInClause(permissionIds.size());
            List<Object> args = new ArrayList<>();
            args.add(role.getId());
            args.addAll(Utility.mapToSet(permissionIds, DomainId::getDomainId));
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_EXT_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL,
                        inClause),
                    args.toArray()
                );
        }
    }

    @Override
    public void remove(PermissionId permissionId) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_EXT_MAP_PERMISSION_SQL,
                permissionId.getDomainId()
            );
    }
}
