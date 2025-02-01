package com.mt.access.port.adapter.persistence.sub_request;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.access.domain.model.sub_request.SubRequestId;
import com.mt.access.domain.model.sub_request.SubRequestQuery;
import com.mt.access.domain.model.sub_request.SubRequestRepository;
import com.mt.access.domain.model.sub_request.SubRequestStatus;
import com.mt.access.domain.model.user.UserId;
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
public class JdbcSubRequestRepository implements SubRequestRepository {

    private static final String INSERT_SQL = "INSERT INTO sub_request " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "project_id, " +
        "domain_id, " +
        "endpoint_id, " +
        "endpoint_project_id, " +
        "replenish_rate, " +
        "burst_capacity, " +
        "rejected_by, " +
        "approved_by, " +
        "rejection_reason, " +
        "sub_request_status" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_SQL = "DELETE FROM sub_request sr WHERE sr.id = ?";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM sub_request sr WHERE sr.domain_id = ? ";
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT * FROM sub_request sr WHERE %s ORDER BY sr.id ASC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM sub_request sr WHERE %s";
    private static final String FIND_BY_EP_IDS_SQL = "SELECT * FROM sub_request sr " +
        "WHERE sr.endpoint_id IN (%s) AND sr.sub_request_status = 'APPROVED' " +
        "GROUP BY sr.endpoint_id " +
        "HAVING MAX(sr.modified_at) > 0 ORDER BY sr.id ASC LIMIT ? OFFSET ?";
    private static final String COUNT_BY_EP_IDS_SQL =
        "SELECT COUNT(*) AS count FROM sub_request sr WHERE sr.id IN " +
            "(SELECT sr2.id FROM sub_request sr2 WHERE sr2.endpoint_id IN (%s) AND sr2.sub_request_status = 'APPROVED' " +
            "GROUP BY sr2.endpoint_id " +
            "HAVING MAX(sr2.modified_at) > 0)";
    private static final String FIND_MY_BY_EP_IDS_SQL = "SELECT * FROM sub_request sr " +
        "WHERE sr.created_by = ? AND sr.sub_request_status = 'APPROVED' " +
        "GROUP BY sr.endpoint_id HAVING MAX(sr.modified_at) > 0 ORDER BY sr.id ASC LIMIT ? OFFSET ?";
    private static final String COUNT_MY_BY_EP_IDS_SQL =
        "SELECT COUNT(*) AS count FROM sub_request sr WHERE sr.id IN " +
            "(" +
            "SELECT sr2.id FROM sub_request sr2 WHERE sr2.created_by = ? AND sr2.sub_request_status = 'APPROVED' " +
            "GROUP BY sr2.endpoint_id HAVING MAX(sr2.modified_at) > 0" +
            ")";
    private static final String FIND_SUBSCRIBER_SQL =
        "SELECT DISTINCT sr.project_id FROM sub_request sr " +
            "WHERE sr.endpoint_id = ? and sr.sub_request_status = 'APPROVED'";
    private static final String FIND_SUBSCRIBE_EP_SQL =
        "SELECT DISTINCT sr.endpoint_id FROM sub_request sr " +
            "WHERE sr.project_id = ? and sr.sub_request_status = 'APPROVED'";
    private static final String UPDATE_SQL = "UPDATE sub_request sr SET " +
        "sr.modified_at = ? ," +
        "sr.modified_by = ?, " +
        "sr.version = ?, " +
        "sr.replenish_rate = ?, " +
        "sr.burst_capacity = ?, " +
        "sr.rejected_by = ?, " +
        "sr.approved_by = ?, " +
        "sr.rejection_reason = ?, " +
        "sr.sub_request_status = ? " +
        "WHERE sr.id = ? AND sr.version = ? ";

    @Override
    public void add(SubRequest subRequest) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                subRequest.getId(),
                subRequest.getCreatedAt(),
                subRequest.getCreatedBy(),
                subRequest.getModifiedAt(),
                subRequest.getModifiedBy(),
                0,
                subRequest.getProjectId().getDomainId(),
                subRequest.getSubRequestId().getDomainId(),
                subRequest.getEndpointId().getDomainId(),
                subRequest.getEndpointProjectId().getDomainId(),
                subRequest.getReplenishRate(),
                subRequest.getBurstCapacity(),
                Checker.isNull(subRequest.getRejectionBy()) ? null :
                    subRequest.getRejectionBy().getDomainId(),
                Checker.isNull(subRequest.getApprovedBy()) ? null :
                    subRequest.getApprovedBy().getDomainId(),
                subRequest.getRejectionReason(),
                subRequest.getSubRequestStatus().name()
            );
    }

    @Override
    public SumPagedRep<SubRequest> query(SubRequestQuery query) {
        List<String> whereClause = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getIds())) {
            String inClause = DatabaseUtility.getInClause(query.getIds().size());
            String byDomainIds = String.format("sr.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Checker.notNull(query.getCreatedBy())) {
            String byCreatedBy = "sr.created_by = ?";
            whereClause.add(byCreatedBy);
        }
        if (Checker.notNullOrEmpty(query.getEpProjectIds())) {
            String inClause = DatabaseUtility.getInClause(query.getEpProjectIds().size());
            String byProjectIds = String.format("sr.endpoint_project_id IN (%s)", inClause);
            whereClause.add(byProjectIds);
        }
        if (Checker.notNullOrEmpty(query.getSubRequestStatuses())) {
            String inClause = DatabaseUtility.getInClause(query.getSubRequestStatuses().size());
            String byStatus = String.format("sr.sub_request_status IN (%s)", inClause);
            whereClause.add(byStatus);
        }
        String join = String.join(" AND ", whereClause);
        String dataQuery = String.format(DYNAMIC_DATA_QUERY_SQL, join);
        String countQuery = String.format(DYNAMIC_COUNT_QUERY_SQL, join);
        List<Object> args = new ArrayList<>();
        if (Checker.notNullOrEmpty(query.getIds())) {
            args.addAll(
                query.getIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        }
        if (Checker.notNull(query.getCreatedBy())) {
            args.add(
                query.getCreatedBy().getDomainId());
        }
        if (Checker.notNullOrEmpty(query.getEpProjectIds())) {
            args.addAll(
                query.getEpProjectIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Checker.notNullOrEmpty(query.getSubRequestStatuses())) {
            args.addAll(
                query.getSubRequestStatuses().stream().map(Enum::name).collect(Collectors.toSet()));
        }
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(countQuery,
                new DatabaseUtility.ExtractCount(),
                args.toArray()
            );
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<SubRequest> data = CommonDomainRegistry.getJdbcTemplate()
            .query(dataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public SumPagedRep<SubRequest> getMySubscriptions(SubRequestQuery query) {
        List<Object> args = new ArrayList<>();
        args.add(query.getCreatedBy().getDomainId());
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<SubRequest> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_MY_BY_EP_IDS_SQL,
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_MY_BY_EP_IDS_SQL,
                new DatabaseUtility.ExtractCount(),
                query.getCreatedBy().getDomainId()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public void remove(SubRequest subRequest) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_SQL,
                subRequest.getId()
            );
    }

    @Override
    public SubRequest query(SubRequestId id) {
        List<SubRequest> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                id.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public Set<ProjectId> getSubProjectId(EndpointId endpointId) {
        List<ProjectId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_SUBSCRIBER_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<ProjectId> list = new ArrayList<>();
                    do {
                        list.add(new ProjectId(rs.getString("project_id")));
                    } while (rs.next());
                    return list;
                },
                endpointId.getDomainId()
            );
        return new HashSet<>(data);
    }

    @Override
    public SumPagedRep<SubRequest> getSubscription(SubRequestQuery query) {
        String inSql = DatabaseUtility.getInClause(query.getEpIds().size());
        List<Object> args = query.getEpIds().stream().map(DomainId::getDomainId).distinct()
            .collect(Collectors.toList());
        args.add(query.getPageConfig().getPageSize());
        args.add(query.getPageConfig().getOffset());

        List<SubRequest> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format(FIND_BY_EP_IDS_SQL, inSql),
                new RowMapper(),
                args.toArray()
            );
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format(COUNT_BY_EP_IDS_SQL, inSql),
                new DatabaseUtility.ExtractCount(),
                query.getEpIds().stream().map(DomainId::getDomainId).distinct().toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Set<EndpointId> getSubscribeEndpointIds(ProjectId projectId) {
        List<EndpointId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_SUBSCRIBE_EP_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<EndpointId> list = new ArrayList<>();
                    do {
                        list.add(new EndpointId(rs.getString("endpoint_id")));
                    } while (rs.next());
                    return list;
                },
                projectId.getDomainId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void update(SubRequest old, SubRequest updated) {
        if (old.sameAs(updated)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getModifiedAt(),
                updated.getModifiedBy(),
                updated.getVersion() + 1,
                updated.getReplenishRate(),
                updated.getBurstCapacity(),
                Checker.isNull(updated.getRejectionBy()) ? null :
                    updated.getRejectionBy().getDomainId(),
                Checker.isNull(updated.getApprovedBy()) ? null :
                    updated.getApprovedBy().getDomainId(),
                updated.getRejectionReason(),
                updated.getSubRequestStatus().name(),
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
    }

    private static class RowMapper implements ResultSetExtractor<List<SubRequest>> {

        @Override
        public List<SubRequest> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<SubRequest> list = new ArrayList<>();
            long currentId = -1L;
            SubRequest subRequest;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    subRequest = SubRequest.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        new SubRequestId(rs.getString("domain_id")),
                        DatabaseUtility.getNullableInteger(rs, "replenish_rate"),
                        DatabaseUtility.getNullableInteger(rs, "burst_capacity"),
                        Checker.notNull(rs.getString("approved_by")) ?
                            new UserId(rs.getString("approved_by")) : null,
                        Checker.notNull(rs.getString("rejected_by")) ?
                            new UserId(rs.getString("rejected_by")) : null,
                        new ProjectId(rs.getString("endpoint_project_id")),
                        rs.getString("rejection_reason"),
                        SubRequestStatus.valueOf(rs.getString("sub_request_status")),
                        new EndpointId(rs.getString("endpoint_id")),
                        new ProjectId(rs.getString("project_id"))
                    );
                    list.add(subRequest);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
