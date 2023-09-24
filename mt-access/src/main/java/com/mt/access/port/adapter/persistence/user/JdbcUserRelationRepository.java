package com.mt.access.port.adapter.persistence.user;

import static com.mt.access.infrastructure.AppConstant.MT_AUTH_PROJECT_ID;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationQuery;
import com.mt.access.domain.model.user.UserRelationRepository;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRelationRepository implements UserRelationRepository {

    private static final String INSERT_SQL = "INSERT INTO user_relation " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "project_id, " +
        "user_id" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?)";
    private static final String INSERT_ROLE_MAP = "INSERT INTO user_relation_role_map " +
        "(" +
        "id, " +
        "role" +
        ") VALUES " +
        "(?,?)";
    private static final String INSERT_TENANT_MAP = "INSERT INTO user_relation_tenant_map " +
        "(" +
        "id, " +
        "tenant" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_SQL = "DELETE FROM user_relation ur WHERE ur.id = ?";
    private static final String DELETE_ROLE_MAP_SQL =
        "DELETE FROM user_relation_role_map urrm WHERE urrm.id = ?";
    private static final String DELETE_TENANT_MAP_SQL =
        "DELETE FROM user_relation_tenant_map urtm WHERE urtm.id = ?";
    private static final String BATCH_DELETE_SQL =
        "DELETE FROM user_relation ur WHERE ur.id IN (%s)";
    private static final String BATCH_DELETE_ROLE_MAP_SQL =
        "DELETE FROM user_relation_role_map urrm WHERE urrm.id (%s)";
    private static final String BATCH_DELETE_ROLE_MAP_BY_ROLE_SQL =
        "DELETE FROM user_relation_role_map urrm WHERE urrm.id = ? AND urrm.role IN (%s)";
    private static final String BATCH_DELETE_TENANT_MAP_BY_TENANT_SQL =
        "DELETE FROM user_relation_tenant_map urtm WHERE urtm.id = ? AND urtm.tenant IN (%s)";
    private static final String BATCH_DELETE_TENANT_MAP_SQL =
        "DELETE FROM user_relation_tenant_map urtm WHERE urtm.id (%s)";
    private static final String FIND_USER_IDS = "SELECT DISTINCT ur.user_id FROM user_relation ur";
    private static final String COUNT_PROJECT_ADMIN =
        "SELECT COUNT(*) AS count FROM user_relation_role_map mt WHERE mt.role = ?";
    private static final String COUNT_PROJECT_OWNED =
        "SELECT COUNT(*) AS count FROM user_relation ur WHERE ur.project_id = ?";
    private static final String FIND_PROJECT_IDS =
        "SELECT DISTINCT ur.project_id FROM user_relation ur";
    private static final String FIND_BY_USER_ID_PROJECT_ID_SQL =
        "SELECT ur.*, m1.role, m2.tenant FROM user_relation ur " +
            "LEFT JOIN user_relation_role_map m1 ON m1.id = ur.id " +
            "LEFT JOIN user_relation_tenant_map m2 ON m2.id = ur.id " +
            "WHERE ur.user_id = ? AND ur.project_id = ?";
    private static final String FIND_BY_USER_ID_SQL =
        "SELECT temp.*, m1.role, m2.tenant " +
            "FROM (SELECT * FROM user_relation ur WHERE ur.user_id = ? ORDER BY ur.id ASC LIMIT ? OFFSET ?) AS temp" +
            "LEFT JOIN user_relation_role_map m1 ON m1.id = temp.id " +
            "LEFT JOIN user_relation_tenant_map m2 ON m2.id = temp.id ";
    private static final String COUNT_BY_USER_ID_SQL =
        "SELECT COUNT(*) AS count FROM user_relation ur WHERE ur.user_id = ?";
    private static final String FIND_EMAIL_LIKE_SQL =
        "SELECT temp.*, m1.role, m2.tenant " +
            "FROM (" +
            "SELECT * FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.email LIKE ? AND ur.project_id = ? ORDER BY ur.id ASC LIMIT ? OFFSET ? " +
            ") AS temp" +
            "LEFT JOIN user_relation_role_map m1 ON m1.id = temp.id " +
            "LEFT JOIN user_relation_tenant_map m2 ON m2.id = temp.id ";
    private static final String COUNT_EMAIL_LIKE_SQL =
        "SELECT COUNT(*) AS count FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.email LIKE ? AND ur.project_id = ?";
    private static final String FIND_BY_ROLE_SQL =
        "SELECT temp.*, m1.role, m2.tenant FROM" +
            "(SELECT ur.* FROM user_relation_role_map mt LEFT JOIN user_relation ur ON mt.id = ur.id " +
            "WHERE mt.role = ? AND ur.project_id = ? LIMIT ? OFFSET ?) AS temp " +
            "LEFT JOIN user_relation_role_map m1 ON m1.id = temp.id " +
            "LEFT JOIN user_relation_tenant_map m2 ON m2.id = temp.id ";
    ;
    private static final String COUNT_BY_ROLE_SQL =
        "SELECT COUNT(DISTINCT mt.id) AS count FROM user_relation_role_map mt LEFT JOIN user_relation ur ON mt.id = ur.id " +
            "WHERE mt.role = ? AND ur.project_id = ?";
    private static final String UPDATE_SQL = "UPDATE user_relation ur SET " +
        "ur.modified_at = ? ," +
        "ur.modified_by = ?, " +
        "ur.version = ? " +
        "WHERE ur.id = ? AND ur.version = ? ";
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT temp.*, m1.role, m2.tenant " +
            "FROM (" +
            "SELECT * FROM user_relation ur " +
            "WHERE %s ORDER BY ur.id ASC LIMIT ? OFFSET ? " +
            ") AS temp " +
            "LEFT JOIN user_relation_role_map m1 ON m1.id = temp.id " +
            "LEFT JOIN user_relation_tenant_map m2 ON m2.id = temp.id ";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM user_relation ur WHERE %s";

    @Override
    public void add(UserRelation userRelation) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                userRelation.getId(),
                userRelation.getCreatedAt(),
                userRelation.getCreatedBy(),
                userRelation.getModifiedAt(),
                userRelation.getModifiedBy(),
                0,
                userRelation.getProjectId().getDomainId(),
                userRelation.getUserId().getDomainId()
            );
        //for mapped tables
        List<BatchInsertKeyValue> standaloneRoles = new ArrayList<>();
        List<BatchInsertKeyValue> tenantIds = new ArrayList<>();
        if (Checker.notNullOrEmpty(userRelation.getStandaloneRoles())) {
            List<BatchInsertKeyValue> collect = userRelation.getStandaloneRoles().stream()
                .map(ee -> new BatchInsertKeyValue(userRelation.getId(), ee.getDomainId())).collect(
                    Collectors.toList());
            standaloneRoles.addAll(collect);
        }
        if (Checker.notNullOrEmpty(userRelation.getTenantIds())) {
            List<BatchInsertKeyValue> collect = userRelation.getTenantIds().stream()
                .map(ee -> new BatchInsertKeyValue(userRelation.getId(), ee.getDomainId())).collect(
                    Collectors.toList());
            tenantIds.addAll(collect);
        }
        if (standaloneRoles.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_ROLE_MAP, standaloneRoles, standaloneRoles.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (tenantIds.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_TENANT_MAP, tenantIds, tenantIds.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public SumPagedRep<UserRelation> query(UserRelationQuery query) {
        if (query.getEmailLike() != null) {
            return searchUserByEmailLike(query);
        }
        if (query.getRoleId() != null) {
            return getUserRelationWithRole(query);
        }

        List<String> whereClause = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getUserIds())) {
            String inClause = DatabaseUtility.getInClause(query.getUserIds().size());
            String byDomainIds = String.format("ur.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNullOrEmpty(query.getProjectIds())) {
            String inClause = DatabaseUtility.getInClause(query.getProjectIds().size());
            String byDomainIds = String.format("ur.project_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        String join = String.join(" AND ", whereClause);
        String finalDataQuery = String.format(DYNAMIC_DATA_QUERY_SQL, join);
        String finalCountQuery = String.format(DYNAMIC_COUNT_QUERY_SQL, join);
        List<Object> args = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getUserIds())) {
            args.addAll(
                query.getUserIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        }
        if (Checker.notNull(query.getProjectIds())) {
            args.addAll(
                query.getProjectIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        Long count;
        count = CommonDomainRegistry.getJdbcTemplate()
            .query(finalCountQuery,
                new DatabaseUtility.ExtractCount(),
                args.toArray()
            );
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public void remove(UserRelation userRelation) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_ROLE_MAP_SQL,
                userRelation.getId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_TENANT_MAP_SQL,
                userRelation.getId()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_SQL,
                userRelation.getId()
            );
    }

    @Override
    public SumPagedRep<UserRelation> get(UserId id) {
        UserQuery query = new UserQuery(id);
        List<Object> args = new ArrayList<>();
        args.add(id.getDomainId());
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());
        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_USER_ID_SQL,
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_BY_USER_ID_SQL,
                new DatabaseUtility.ExtractCount(),
                id.getDomainId()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Optional<UserRelation> query(UserId id, ProjectId projectId) {
        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_USER_ID_PROJECT_ID_SQL,
                new RowMapper(),
                id.getDomainId(),
                projectId.getDomainId()
            );
        return data.isEmpty() ? Optional.empty() : Optional.of(data.get(0));
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
    public long countProjectOwnedTotal(ProjectId projectId) {
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PROJECT_OWNED,
                new DatabaseUtility.ExtractCount(),
                projectId.getDomainId()
            );
        return count;
    }

    @Override
    public long countProjectAdmin(RoleId adminRoleId) {
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PROJECT_ADMIN,
                new DatabaseUtility.ExtractCount(),
                adminRoleId.getDomainId()
            );
        return count;
    }

    @Override
    public void removeAll(Set<UserRelation> allByQuery) {
        Set<Long> ids = allByQuery.stream().map(Auditable::getId).collect(Collectors.toSet());
        String inClause = DatabaseUtility.getInClause(ids.size());
        CommonDomainRegistry.getJdbcTemplate()
            .update(String.format(BATCH_DELETE_ROLE_MAP_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(String.format(BATCH_DELETE_TENANT_MAP_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(String.format(BATCH_DELETE_SQL, inClause),
                ids.toArray()
            );
    }

    @Override
    public Set<UserId> getUserIds() {
        List<UserId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_USER_IDS,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<UserId> list = new ArrayList<>();
                    do {
                        list.add(new UserId(rs.getString("user_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public void update(UserRelation old, UserRelation updated) {
        if (old.sameAs(updated)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getModifiedAt(),
                updated.getModifiedBy(),
                updated.getVersion() + 1,
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
        DatabaseUtility.updateMap(old.getStandaloneRoles(),
            updated.getStandaloneRoles(),
            (added) -> {
                //for linked tables
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_ROLE_MAP, insertKeyValues,
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
                        String.format(BATCH_DELETE_ROLE_MAP_BY_ROLE_SQL,
                            inClause),
                        args.toArray()
                    );
            });
        DatabaseUtility.updateMap(old.getTenantIds(),
            updated.getTenantIds(),
            (added) -> {
                //for linked tables
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_TENANT_MAP, insertKeyValues,
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
                        String.format(BATCH_DELETE_TENANT_MAP_BY_TENANT_SQL,
                            inClause),
                        args.toArray()
                    );
            });
    }

    private static class RowMapper implements ResultSetExtractor<List<UserRelation>> {

        @Override
        public List<UserRelation> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<UserRelation> list = new ArrayList<>();
            long currentId = -1L;
            UserRelation userRelation = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    userRelation = UserRelation.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        new ProjectId(rs.getString("project_id")),
                        new UserId(rs.getString("user_id"))
                    );
                    list.add(userRelation);
                    currentId = dbId;
                }
                Set<RoleId> linkedApiPermissionIds = userRelation.getStandaloneRoles();
                String roleId = rs.getString("role");
                if (Checker.notNull(roleId)) {
                    linkedApiPermissionIds.add(new RoleId(roleId));
                }
                Set<ProjectId> commonPermissionIds = userRelation.getTenantIds();
                String projectId = rs.getString("tenant");
                if (Checker.notNull(projectId)) {
                    commonPermissionIds.add(new ProjectId(projectId));
                }
            } while (rs.next());
            return list;
        }
    }


    private SumPagedRep<UserRelation> searchUserByEmailLike(UserRelationQuery query) {
        String projectId = query.getProjectIds().stream().findFirst().get().getDomainId();
        List<Object> args = new ArrayList<>();
        args.add("%" + query.getEmailLike() + "%");
        args.add(projectId);
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());
        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_EMAIL_LIKE_SQL,
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_EMAIL_LIKE_SQL,
                new DatabaseUtility.ExtractCount(),
                "%" + query.getEmailLike() + "%",
                projectId
            );
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<UserRelation> getUserRelationWithRole(UserRelationQuery query) {

        List<Object> args = new ArrayList<>();
        args.add(query.getRoleId().getDomainId());
        args.add(MT_AUTH_PROJECT_ID);
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());
        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_ROLE_SQL,
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_BY_ROLE_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getRoleId().getDomainId(),
                MT_AUTH_PROJECT_ID
            );
        return new SumPagedRep<>(data, count);
    }
}
