package com.mt.access.port.adapter.persistence.user;

import static com.mt.access.infrastructure.AppConstant.MAIN_PROJECT_ID;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationQuery;
import com.mt.access.domain.model.user.UserRelationRepository;
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
    private static final String FIND_USER_IDS = "SELECT DISTINCT ur.user_id FROM user_relation ur";
    private static final String COUNT_PROJECT_ADMIN =
        "SELECT COUNT(*) AS count FROM user_relation_role_map mt WHERE mt.role = ?";
    private static final String COUNT_PROJECT_OWNED =
        "SELECT COUNT(*) AS count FROM user_relation ur WHERE ur.project_id = ?";
    private static final String FIND_PROJECT_IDS =
        "SELECT DISTINCT ur.project_id FROM user_relation ur";
    private static final String FIND_BY_USER_ID_PROJECT_ID_SQL =
        "SELECT ur.* FROM user_relation ur WHERE ur.user_id = ? AND ur.project_id = ?";
    private static final String FIND_EMAIL_LIKE_SQL =
        "SELECT ur.* FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.email LIKE ? AND ur.project_id = ? ORDER BY ur.id ASC LIMIT ? OFFSET ? ";
    private static final String FIND_MOBILE_LIKE_SQL =
        "SELECT ur.* FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.mobile_number LIKE ? AND ur.project_id = ? ORDER BY ur.id ASC LIMIT ? OFFSET ? ";
    private static final String FIND_USERNAME_LIKE_SQL =
        "SELECT ur.* FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.username LIKE ? AND ur.project_id = ? ORDER BY ur.id ASC LIMIT ? OFFSET ? ";
    private static final String COUNT_EMAIL_LIKE_SQL =
        "SELECT COUNT(*) AS count FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.email LIKE ? AND ur.project_id = ?";
    private static final String COUNT_MOBILE_LIKE_SQL =
        "SELECT COUNT(*) AS count FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.mobile_number LIKE ? AND ur.project_id = ?";
    private static final String COUNT_USERNAME_LIKE_SQL =
        "SELECT COUNT(*) AS count FROM user_relation ur " +
            "LEFT JOIN user_ u ON ur.user_id = u.domain_id " +
            "WHERE u.username LIKE ? AND ur.project_id = ?";
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
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT * FROM user_relation ur " +
            "WHERE %s ORDER BY ur.id ASC LIMIT ? OFFSET ? ";
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
    }

    @Override
    public SumPagedRep<UserRelation> query(UserRelationQuery query) {
        if (query.getEmail() != null) {
            return searchUserByEmailLike(query);
        }
        if (query.getMobileNumber() != null) {
            return searchUserByMobileNumberLike(query);
        }
        if (query.getUsername() != null) {
            return searchUserByUsernameLike(query);
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

    private SumPagedRep<UserRelation> searchUserByEmailLike(UserRelationQuery query) {
        String projectId = query.getProjectIds().stream().findFirst().get().getDomainId();
        List<Object> args = new ArrayList<>();
        args.add(query.getEmail() + "%");
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
                query.getEmail() + "%",
                projectId
            );
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<UserRelation> searchUserByMobileNumberLike(UserRelationQuery query) {
        String projectId = query.getProjectIds().stream().findFirst().get().getDomainId();
        List<Object> args = new ArrayList<>();
        args.add(query.getMobileNumber() + "%");
        args.add(projectId);
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());
        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_MOBILE_LIKE_SQL,
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_MOBILE_LIKE_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getMobileNumber() + "%",
                projectId
            );
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<UserRelation> searchUserByUsernameLike(UserRelationQuery query) {
        String projectId = query.getProjectIds().stream().findFirst().get().getDomainId();
        List<Object> args = new ArrayList<>();
        args.add(query.getUsername() + "%");
        args.add(projectId);
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());
        List<UserRelation> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_USERNAME_LIKE_SQL,
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_USERNAME_LIKE_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getUsername() + "%",
                projectId
            );
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<UserRelation> getUserRelationWithRole(UserRelationQuery query) {

        List<Object> args = new ArrayList<>();
        args.add(query.getRoleId().getDomainId());
        args.add(MAIN_PROJECT_ID);
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
                MAIN_PROJECT_ID
            );
        return new SumPagedRep<>(data, count);
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
            } while (rs.next());
            return list;
        }
    }
}
