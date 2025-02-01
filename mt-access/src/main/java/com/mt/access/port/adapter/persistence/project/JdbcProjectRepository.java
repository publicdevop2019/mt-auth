package com.mt.access.port.adapter.persistence.project;

import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.project.ProjectRepository;
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
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcProjectRepository implements ProjectRepository {
    private static final String INSERT_SQL = "INSERT INTO project " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "name, " +
        "domain_id" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?)";
    private static final String COUNT_PROJECT_TOTAL = "SELECT COUNT(*) AS count FROM project";
    private static final String FIND_ALL_PROJECT_IDS = "SELECT p.domain_id FROM project p";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM project p WHERE p.domain_id = ?";

    private static final String FIND_BY_DOMAIN_IDS_SQL =
        "SELECT * FROM project p WHERE p.domain_id IN (%s) ORDER BY p.id ASC LIMIT ? OFFSET ?";
    private static final String COUNT_BY_DOMAIN_IDS_SQL =
        "SELECT COUNT(*) AS count FROM project p " +
            "WHERE p.domain_id IN (%s) ";

    private static final String FIND_BY_DEFAULT_SQL =
        "SELECT * FROM project p ORDER BY p.id ASC LIMIT ? OFFSET ?";
    private static final String COUNT_BY_DEFAULT_SQL = "SELECT COUNT(*) AS count FROM project p";

    @Override
    public void add(Project project) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                project.getId(),
                project.getCreatedAt(),
                project.getCreatedBy(),
                project.getModifiedAt(),
                project.getModifiedBy(),
                0,
                project.getName(),
                project.getProjectId().getDomainId()
            );
    }

    @Override
    public SumPagedRep<Project> query(ProjectQuery query) {
        if (Checker.notNullOrEmpty(query.getIds())) {
            return queryByIds(query);
        } else {
            return defaultQuery(query);
        }
    }

    private SumPagedRep<Project> defaultQuery(ProjectQuery query) {
        List<Project> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DEFAULT_SQL,
                new RowMapper(),
                query.getPageConfig().getPageSize(),
                query.getPageConfig().getOffset()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_BY_DEFAULT_SQL,
                new DatabaseUtility.ExtractCount()
            );
        return new SumPagedRep<>(data, count);
    }

    private SumPagedRep<Project> queryByIds(ProjectQuery query) {
        String inSql = DatabaseUtility.getInClause(query.getIds().size());
        List<Object> args = query.getIds().stream().map(DomainId::getDomainId).distinct()
            .collect(Collectors.toList());
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<Project> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format(FIND_BY_DOMAIN_IDS_SQL, inSql),
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format(COUNT_BY_DOMAIN_IDS_SQL, inSql),
                new DatabaseUtility.ExtractCount(),
                query.getIds().stream().map(DomainId::getDomainId).distinct().toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Project query(ProjectId id) {
        List<Project> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                id.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public Set<ProjectId> allProjectIds() {
        List<ProjectId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_PROJECT_IDS,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<ProjectId> list = new ArrayList<>();
                    do {
                        list.add(new ProjectId(rs.getString("domain_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public long countTotal() {
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PROJECT_TOTAL,
                new DatabaseUtility.ExtractCount()
            );
        return count;
    }


    private static class RowMapper implements ResultSetExtractor<List<Project>> {

        @Override
        public List<Project> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Project> list = new ArrayList<>();
            long currentId = -1L;
            Project project = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    project = Project.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        rs.getString("name"),
                        new ProjectId(rs.getString("domain_id"))
                    );
                    list.add(project);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
