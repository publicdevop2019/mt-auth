package com.mt.access.port.adapter.persistence.permission;

import com.mt.access.domain.model.permission.LinkedApiPermissionIdRepository;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.validate.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLinkedPermissionIdRepository implements LinkedApiPermissionIdRepository {
    private static final String INSERT_LINKED_PERMISSION_MAP_SQL =
        "INSERT INTO linked_permission_ids_map " +
            "(" +
            "id, " +
            "domain_id" +
            ") VALUES " +
            "(?,?)";
    private static final String FIND_LINKED_PERMISSION_ID_BY_ID_SQL =
        "SELECT * FROM linked_permission_ids_map t WHERE t.id = ?";
    private static final String DELETE_LINKED_API_PERMISSION_BY_ID_SQL =
        "DELETE FROM linked_permission_ids_map lpm WHERE lpm.id = ?";
    private static final String DELETE_LINKED_API_PERMISSION_BY_DOMAIN_ID_SQL =
        "DELETE FROM linked_permission_ids_map lpm WHERE lpm.domain_id = ?";

    @Override
    public Set<PermissionId> query(Permission permission) {
        List<PermissionId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_LINKED_PERMISSION_ID_BY_ID_SQL,
                new RowMapper(),
                permission.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void add(Permission permission, Set<PermissionId> permissionIds) {
        if (Utility.notNullOrEmpty(permissionIds)) {
            Set<BatchInsertKeyValue> args = Utility.mapToSet(permissionIds,
                ee -> new BatchInsertKeyValue(permission.getId(), ee.getDomainId()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_LINKED_PERMISSION_MAP_SQL, args,
                    args.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void removeAll(Permission permission, Set<PermissionId> permissionIds) {
        if (Utility.notNullOrEmpty(permissionIds)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_LINKED_API_PERMISSION_BY_ID_SQL,
                    permission.getId()
                );
        }
    }

    @Override
    public void addAll(Map<Permission, Set<PermissionId>> permissionLinkMap) {
        List<BatchInsertKeyValue> linkedPermList = new ArrayList<>();
        permissionLinkMap.forEach((k, v) -> {
            if (Utility.notNullOrEmpty(v)) {
                Set<BatchInsertKeyValue> args =
                    Utility.mapToSet(v, (e) -> new BatchInsertKeyValue(k.getId(), e.getDomainId()));
                linkedPermList.addAll(args);
            }
        });
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate(INSERT_LINKED_PERMISSION_MAP_SQL, linkedPermList, linkedPermList.size(),
                (ps, permission) -> {
                    ps.setLong(1, permission.getId());
                    ps.setString(2, permission.getValue());
                });
    }

    private static class RowMapper implements ResultSetExtractor<List<PermissionId>> {

        @Override
        public List<PermissionId> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<PermissionId> list = new ArrayList<>();
            do {
                String idRaw = rs.getString("domain_id");
                if (Utility.notNull(idRaw)) {
                    list.add(new PermissionId(idRaw));
                }
            } while (rs.next());
            return list;
        }
    }

    @Override
    public void remove(PermissionId permissionId) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_LINKED_API_PERMISSION_BY_DOMAIN_ID_SQL,
                permissionId.getDomainId()
            );
    }
}
