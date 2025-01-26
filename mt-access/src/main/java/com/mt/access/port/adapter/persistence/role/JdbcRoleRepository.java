package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleRepository;
import com.mt.access.domain.model.role.RoleType;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class JdbcRoleRepository implements RoleRepository {


    private static final String INSERT_SQL = "INSERT INTO role " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "name, " +
        "description, " +
        "parent_id, " +
        "domain_id, " +
        "project_id, " +
        "system_create, " +
        "tenant_id, " +
        "type" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_COMMON_MAP = "INSERT INTO role_common_permission_map " +
        "(" +
        "id, " +
        "permission" +
        ") VALUES " +
        "(?,?)";
    private static final String INSERT_API_MAP = "INSERT INTO role_api_permission_map " +
        "(" +
        "id, " +
        "permission" +
        ") VALUES " +
        "(?,?)";
    private static final String INSERT_EXT_MAP = "INSERT INTO role_external_permission_map " +
        "(" +
        "id, " +
        "permission" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_SQL = "DELETE FROM role r WHERE r.id = ?";
    private static final String DELETE_COMMON_SQL =
        "DELETE FROM role_common_permission_map map WHERE map.id = ?";
    private static final String DELETE_API_SQL =
        "DELETE FROM role_api_permission_map map WHERE map.id = ?";
    private static final String DELETE_EXT_SQL =
        "DELETE FROM role_external_permission_map map WHERE map.id = ?";
    private static final String DELETE_API_MAP_PERMISSION_SQL =
        "DELETE FROM role_common_permission_map map WHERE map.permission = ?";
    private static final String DELETE_COMMON_MAP_PERMISSION_SQL =
        "DELETE FROM role_api_permission_map map WHERE map.permission = ?";
    private static final String DELETE_EXT_MAP_PERMISSION_SQL =
        "DELETE FROM role_external_permission_map map WHERE map.permission = ?";
    private static final String FIND_PROJECT_IDS =
        "SELECT DISTINCT r.project_id FROM role r";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM role r WHERE r.domain_id = ? ";
    private static final String FIND_COMMON_PERMISSION = "SELECT m.permission " +
        "FROM role_common_permission_map m WHERE m.id = ?";
    private static final String FIND_API_PERMISSION = "SELECT m.permission " +
        "FROM role_api_permission_map m WHERE m.id = ?";
    private static final String FIND_EXT_PERMISSION = "SELECT m.permission " +
        "FROM role_external_permission_map m WHERE m.id = ?";
    private static final String COUNT_PROJECT_CREATED_TOTAL =
        "SELECT COUNT(*) AS count FROM role r " +
            "WHERE r.project_id = ? AND r.type = 'USER'";
    private static final String FIND_PROJECT_ROOT_SQL =
        "SELECT * FROM role r WHERE r.type = 'CLIENT_ROOT' AND r.project_id = ?";
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT * FROM role r WHERE %s ORDER BY r.id ASC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM role r WHERE %s";
    private static final String UPDATE_SQL = "UPDATE role r SET " +
        "r.modified_at = ? ," +
        "r.modified_by = ?, " +
        "r.version = ?, " +
        "r.name = ?, " +
        "r.description = ?, " +
        "r.parent_id = ? " +
        "WHERE r.id = ? AND r.version = ? ";
    private static final String BATCH_DELETE_COMMON_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM role_common_permission_map rcpm WHERE rcpm.id = ? AND rcpm.permission IN (%s)";
    private static final String BATCH_DELETE_API_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM role_api_permission_map rapm WHERE rapm.id = ? AND rapm.permission IN (%s)";
    private static final String BATCH_DELETE_EXT_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM role_external_permission_map repm WHERE repm.id = ? AND repm.permission IN (%s)";

    @Override
    public void add(Role role) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                role.getId(),
                role.getCreatedAt(),
                role.getCreatedBy(),
                role.getModifiedAt(),
                role.getModifiedBy(),
                0,
                role.getName(),
                role.getDescription(),
                role.getParentId() == null ? null :
                    role.getParentId().getDomainId(),
                role.getRoleId().getDomainId(),
                role.getProjectId().getDomainId(),
                role.getSystemCreate(),
                role.getTenantId() == null ? null :
                    role.getTenantId().getDomainId(),
                role.getType().name()
            );
        //for mapped tables
        List<BatchInsertKeyValue> commonPermList = new ArrayList<>();
        List<BatchInsertKeyValue> apiPermList = new ArrayList<>();
        List<BatchInsertKeyValue> extPermList = new ArrayList<>();
        if (Utility.notNullOrEmpty(role.getCommonPermissionIds())) {
            List<BatchInsertKeyValue> collect = role.getCommonPermissionIds().stream()
                .map(ee -> new BatchInsertKeyValue(role.getId(), ee.getDomainId())).collect(
                    Collectors.toList());
            commonPermList.addAll(collect);
        }
        if (Utility.notNullOrEmpty(role.getApiPermissionIds())) {
            List<BatchInsertKeyValue> collect = role.getApiPermissionIds().stream()
                .map(ee -> new BatchInsertKeyValue(role.getId(), ee.getDomainId())).collect(
                    Collectors.toList());
            apiPermList.addAll(collect);
        }
        if (Utility.notNullOrEmpty(role.getExternalPermissionIds())) {
            List<BatchInsertKeyValue> collect = role.getExternalPermissionIds().stream()
                .map(ee -> new BatchInsertKeyValue(role.getId(), ee.getDomainId())).collect(
                    Collectors.toList());
            extPermList.addAll(collect);
        }
        if (commonPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_COMMON_MAP, commonPermList, commonPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (apiPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_API_MAP, apiPermList, apiPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (extPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_EXT_MAP, extPermList, extPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void addAll(Set<Role> roles) {
        List<Role> arrayList = new ArrayList<>(roles);
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate(INSERT_SQL, arrayList, roles.size(),
                (ps, role) -> {
                    ps.setLong(1, role.getId());
                    ps.setLong(2, Instant.now().toEpochMilli());
                    ps.setString(3, "NOT_HTTP");
                    ps.setLong(4, Instant.now().toEpochMilli());
                    ps.setString(5, "NOT_HTTP");
                    ps.setLong(6, 0L);
                    ps.setString(7, role.getName());
                    ps.setString(8, role.getDescription());
                    ps.setString(9, role.getParentId() == null ? null :
                        role.getParentId().getDomainId());
                    ps.setString(10, role.getRoleId().getDomainId());
                    ps.setString(11, role.getProjectId().getDomainId());
                    ps.setBoolean(12, role.getSystemCreate());
                    ps.setString(13, role.getTenantId() == null ? null :
                        role.getTenantId().getDomainId());
                    ps.setString(14, role.getType().name());
                });
        //for mapped tables
        List<BatchInsertKeyValue> commonPermList = new ArrayList<>();
        List<BatchInsertKeyValue> apiPermList = new ArrayList<>();
        List<BatchInsertKeyValue> extPermList = new ArrayList<>();
        roles.forEach(e -> {
            if (Utility.notNullOrEmpty(e.getCommonPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getCommonPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                commonPermList.addAll(collect);
            }
            if (Utility.notNullOrEmpty(e.getApiPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getApiPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                apiPermList.addAll(collect);
            }
            if (Utility.notNullOrEmpty(e.getExternalPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getExternalPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                extPermList.addAll(collect);
            }
        });
        if (commonPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_COMMON_MAP, commonPermList, commonPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (apiPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_API_MAP, apiPermList, apiPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (extPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_EXT_MAP, extPermList, extPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public Set<PermissionId> findCommonPermission(Role role) {
        List<PermissionId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_COMMON_PERMISSION,
                new PermissionIdRowMapper(),
                role.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public Set<PermissionId> findApiPermission(Role role) {
        List<PermissionId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_API_PERMISSION,
                new PermissionIdRowMapper(),
                role.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public Set<PermissionId> findExtPermission(Role role) {
        List<PermissionId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_EXT_PERMISSION,
                new PermissionIdRowMapper(),
                role.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public SumPagedRep<Role> query(RoleQuery query) {
        List<String> whereClause = new ArrayList<>();
        if (Utility.notNullOrEmpty(query.getIds())) {
            String inClause = DatabaseUtility.getInClause(query.getIds().size());
            String byDomainIds = String.format("r.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Utility.notNull(query.getParentId())) {
            String byParentId = "r.parent_id = ?";
            whereClause.add(byParentId);
        }
        if (Utility.notNull(query.getParentIdNull())) {
            String byParentId = "r.parent_id IS NULL";
            whereClause.add(byParentId);
        }
        if (Utility.notNullOrEmpty(query.getProjectIds())) {
            String inClause = DatabaseUtility.getInClause(query.getProjectIds().size());
            String byProjectIds = String.format("r.project_id IN (%s)", inClause);
            whereClause.add(byProjectIds);
        }
        if (Utility.notNullOrEmpty(query.getTenantIds())) {
            String inClause = DatabaseUtility.getInClause(query.getTenantIds().size());
            String byTenantIds = String.format("r.tenant_id IN (%s)", inClause);
            whereClause.add(byTenantIds);
        }
        if (Utility.notNullOrEmpty(query.getNames())) {
            String inClause = DatabaseUtility.getInClause(query.getNames().size());
            String byNames = String.format("r.name IN (%s)", inClause);
            whereClause.add(byNames);
        }
        if (Utility.notNullOrEmpty(query.getTypes())) {
            String inClause = DatabaseUtility.getInClause(query.getTypes().size());
            String byTypes = String.format("r.type IN (%s)", inClause);
            whereClause.add(byTypes);
        }
        String join = String.join(" AND ", whereClause);
        String finalDataQuery;
        String finalCountQuery;
        if (!whereClause.isEmpty()) {
            finalDataQuery = String.format(DYNAMIC_DATA_QUERY_SQL, join);
            finalCountQuery = String.format(DYNAMIC_COUNT_QUERY_SQL, join);
        } else {
            finalDataQuery = DYNAMIC_DATA_QUERY_SQL.replace(" WHERE %s", "");
            finalCountQuery = DYNAMIC_COUNT_QUERY_SQL.replace(" WHERE %s", "");
        }
        List<Object> args = new ArrayList<>();
        if (Utility.notNullOrEmpty(query.getIds())) {
            args.addAll(
                query.getIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        }
        if (Utility.notNull(query.getParentId())) {
            args.add(
                query.getParentId().getDomainId());
        }
        if (Utility.notNullOrEmpty(query.getProjectIds())) {
            args.addAll(
                query.getProjectIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Utility.notNullOrEmpty(query.getTenantIds())) {
            args.addAll(
                query.getTenantIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Utility.notNullOrEmpty(query.getNames())) {
            args.addAll(
                query.getNames());
        }
        if (Utility.notNullOrEmpty(query.getTypes())) {
            args.addAll(
                query.getTypes().stream().map(Enum::name).collect(Collectors.toSet()));
        }
        Long count;
        if (args.isEmpty()) {
            count = CommonDomainRegistry.getJdbcTemplate()
                .query(finalCountQuery,
                    new DatabaseUtility.ExtractCount()
                );
        } else {
            count = CommonDomainRegistry.getJdbcTemplate()
                .query(finalCountQuery,
                    new DatabaseUtility.ExtractCount(),
                    args.toArray()
                );
        }
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());
        List<Role> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public void remove(Role role) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_COMMON_SQL,
                role.getId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_API_SQL,
                role.getId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_EXT_SQL,
                role.getId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_SQL,
                role.getId()
            );
    }

    @Override
    public Role query(RoleId id) {
        List<Role> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                id.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public Set<ProjectId> getProjectIds() {
        List<ProjectId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_PROJECT_IDS,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<ProjectId> list = new ArrayList<>();
                    do {
                        list.add(new ProjectId(rs.getString("project_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public long countProjectCreateTotal(ProjectId projectId) {
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PROJECT_CREATED_TOTAL,
                new DatabaseUtility.ExtractCount(),
                projectId.getDomainId()
            );
        return count;
    }

    @Override
    public Optional<Role> queryClientRoot(ProjectId projectId) {
        List<Role> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_PROJECT_ROOT_SQL,
                new RowMapper(),
                projectId.getDomainId()
            );
        return data.isEmpty() ? Optional.empty() : Optional.of(data.get(0));
    }

    @Override
    public void update(Role old, Role updated) {
        if (old.sameAs(updated)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getModifiedAt(),
                updated.getModifiedBy(),
                updated.getVersion() + 1,
                updated.getName(),
                updated.getDescription(),
                Optional.ofNullable(updated.getParentId()).map(DomainId::getDomainId).orElse(null),
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
        DatabaseUtility.updateMap(old.getCommonPermissionIds(),
            updated.getCommonPermissionIds(),
            (added) -> {
                //for linked tables
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_COMMON_MAP, insertKeyValues,
                        insertKeyValues.size(),
                        (ps, perm) -> {
                            ps.setLong(1, perm.getId());
                            ps.setString(2, perm.getValue());
                        });
            }, (removed) -> {
                String inClause = DatabaseUtility.getInClause(removed.size());
                List<Object> args = new ArrayList<>();
                args.add(old.getId());
                args.addAll(
                    removed.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_COMMON_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL,
                            inClause),
                        args.toArray()
                    );
            });
        DatabaseUtility.updateMap(old.getApiPermissionIds(),
            updated.getApiPermissionIds(),
            (added) -> {
                //for linked tables
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_API_MAP, insertKeyValues,
                        insertKeyValues.size(),
                        (ps, perm) -> {
                            ps.setLong(1, perm.getId());
                            ps.setString(2, perm.getValue());
                        });
            }, (removed) -> {
                String inClause = DatabaseUtility.getInClause(removed.size());
                List<Object> args = new ArrayList<>();
                args.add(old.getId());
                args.addAll(
                    removed.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_API_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL,
                            inClause),
                        args.toArray()
                    );
            });
        DatabaseUtility.updateMap(old.getExternalPermissionIds(),
            updated.getExternalPermissionIds(),
            (added) -> {
                //for linked tables
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_EXT_MAP, insertKeyValues,
                        insertKeyValues.size(),
                        (ps, perm) -> {
                            ps.setLong(1, perm.getId());
                            ps.setString(2, perm.getValue());
                        });
            }, (removed) -> {
                String inClause = DatabaseUtility.getInClause(removed.size());
                List<Object> args = new ArrayList<>();
                args.add(old.getId());
                args.addAll(
                    removed.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_EXT_PERMISSION_BY_ID_AND_DOMAIN_ID_SQL,
                            inClause),
                        args.toArray()
                    );
            });

    }

    @Override
    public void removeReferredPermissionId(PermissionId permissionId) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_API_MAP_PERMISSION_SQL,
                permissionId.getDomainId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_COMMON_MAP_PERMISSION_SQL,
                permissionId.getDomainId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_EXT_MAP_PERMISSION_SQL,
                permissionId.getDomainId()
            );
    }

    private static class RowMapper implements ResultSetExtractor<List<Role>> {

        @Override
        public List<Role> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Role> list = new ArrayList<>();
            long currentId = -1L;
            Role role = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    role = Role.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        rs.getString("name"),
                        rs.getString("description"),
                        new RoleId(rs.getString("domain_id")),
                        Utility.notNull(rs.getString("parent_id")) ?
                            new RoleId(rs.getString("parent_id")) : null,
                        new ProjectId(rs.getString("project_id")),
                        DatabaseUtility.getNullableBoolean(rs, "system_create"),
                        Utility.notNull(rs.getString("tenant_id")) ?
                            new ProjectId(rs.getString("tenant_id")) : null,
                        RoleType.valueOf(rs.getString("type"))
                    );
                    list.add(role);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }

    private static class PermissionIdRowMapper implements ResultSetExtractor<List<PermissionId>> {

        @Override
        public List<PermissionId> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<PermissionId> list = new ArrayList<>();
            do {
                String idRaw = rs.getString("permission");
                if (Utility.notNull(idRaw)) {
                    list.add(new PermissionId(idRaw));
                }
            } while (rs.next());
            return list;
        }
    }
}
