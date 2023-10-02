package com.mt.common.port.adapter.persistence.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobQuery;
import com.mt.common.domain.model.job.JobRepository;
import com.mt.common.domain.model.job.JobStatus;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcJobRepository implements JobRepository {
    private static final String INSERT_SQL = "INSERT INTO job_detail " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "name, " +
        "last_status, " +
        "type, " +
        "failure_count, " +
        "failure_reason, " +
        "failure_allowed, " +
        "max_lock_acquire_failure_allowed, " +
        "notified_admin, " +
        "last_execution, " +
        "domain_id, " +
        "minimum_idle_time_milli" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM job_detail jd WHERE jd.domain_id = ?";
    private static final String NOTIFY_SQL =
        "UPDATE job_detail jb SET jb.notified_admin = true WHERE jb.domain_id = ?";
    private static final String DYNAMIC_DATA_QUERY_SQL = "SELECT * FROM job_detail jd WHERE %s ORDER BY jd.id ASC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM job_detail jd WHERE %s";

    private static final String UPDATE_SQL = "UPDATE job_detail jb SET " +
        "jb.modified_at = ? ," +
        "jb.modified_by = ?, " +
        "jb.version = ? ," +
        "jb.name = ? ," +
        "jb.last_status = ? ," +
        "jb.type = ? ," +
        "jb.failure_count = ? ," +
        "jb.failure_reason = ? ," +
        "jb.failure_allowed = ? ," +
        "jb.max_lock_acquire_failure_allowed = ? ," +
        "jb.notified_admin = ? ," +
        "jb.last_execution = ? ," +
        "jb.domain_id = ? ," +
        "jb.minimum_idle_time_milli = ? " +
        "WHERE jb.id = ? AND jb.version = ? ";

    @Override
    public Set<JobDetail> getByQuery(JobQuery query) {
        return QueryUtility
            .getAllByQuery(this::getPaged, query);
    }

    private SumPagedRep<JobDetail> getPaged(JobQuery query) {
        List<String> whereClause = new ArrayList<>();
        if (Checker.notNull(query.getId())) {
            whereClause.add("jd.domain_id = ?");
        }
        if (Checker.notNull(query.getName())) {
            whereClause.add("jd.name = ?");
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
        if (Checker.notNull(query.getId())) {
            args.add(query.getId());
        }
        if (Checker.notNull(query.getName())) {
            args.add(query.getName());
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
        List<JobDetail> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Optional<JobDetail> getByName(String name) {
        return getPaged(JobQuery.byName(name)).findFirst();
    }

    @Override
    public void add(JobDetail job) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                job.getId(),
                job.getCreatedAt(),
                job.getCreatedBy(),
                job.getModifiedAt(),
                job.getModifiedBy(),
                job.getVersion(),
                job.getName(),
                job.getLastStatus().name(),
                job.getType().name(),
                job.getFailureCount(),
                job.getFailureReason(),
                job.getFailureAllowed(),
                job.getMaxLockAcquireFailureAllowed(),
                job.getNotifiedAdmin(),
                job.getLastExecution(),
                job.getJobId().getDomainId(),
                job.getMinimumIdleTimeMilli()
            );
    }

    @Override
    public void update(JobDetail old, JobDetail updated) {
        if (updated.sameAs(old)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getModifiedAt(),
                updated.getModifiedBy(),
                updated.getVersion(),
                updated.getName(),
                updated.getLastStatus().name(),
                updated.getType().name(),
                updated.getFailureCount(),
                updated.getFailureReason(),
                updated.getFailureAllowed(),
                updated.getMaxLockAcquireFailureAllowed(),
                updated.getNotifiedAdmin(),
                updated.getLastExecution(),
                updated.getJobId().getDomainId(),
                updated.getMinimumIdleTimeMilli(),
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
    }

    @Override
    public JobDetail getById(JobId jobId) {
        List<JobDetail> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                jobId.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public void notifyAdmin(JobId jobId) {
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(NOTIFY_SQL,
                jobId.getDomainId()
            );
        DatabaseUtility.checkUpdate(update);
    }

    private static class RowMapper implements ResultSetExtractor<List<JobDetail>> {

        @Override
        public List<JobDetail> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<JobDetail> list = new ArrayList<>();
            long currentId = -1L;
            JobDetail detail;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    detail = JobDetail.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        rs.getString("name"),
                        Checker.notNull(rs.getString("last_status")) ?
                            JobStatus.valueOf(rs.getString("last_status")) : null,
                        Checker.notNull(rs.getString("type")) ?
                            JobType.valueOf(rs.getString("type")) : null,
                        DatabaseUtility.getNullableInteger(rs, "failure_count"),
                        rs.getString("failure_reason"),
                        DatabaseUtility.getNullableInteger(rs, "failure_allowed"),
                        DatabaseUtility.getNullableInteger(rs, "max_lock_acquire_failure_allowed"),
                        DatabaseUtility.getNullableBoolean(rs, "notified_admin"),
                        DatabaseUtility.getNullableLong(rs, "last_execution"),
                        new JobId(rs.getString("domain_id")),
                        DatabaseUtility.getNullableLong(rs, "minimum_idle_time_milli")
                    );
                    list.add(detail);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
