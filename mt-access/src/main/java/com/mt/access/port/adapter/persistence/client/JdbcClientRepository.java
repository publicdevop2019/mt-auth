package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.ClientRepository;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.ExternalUrl;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcClientRepository implements ClientRepository {

    public static final String RESOURCE_SQL =
        "SELECT c.* FROM resources_map mt LEFT JOIN client c ON mt.id = c.id WHERE mt.domain_id IN (%s)";
    private static final String DYNAMIC_DATA_QUERY_SQL =
        "SELECT * FROM client c WHERE %s ORDER BY c.id ASC LIMIT ? OFFSET ?";
    private static final String DYNAMIC_COUNT_QUERY_SQL =
        "SELECT COUNT(*) AS count FROM client c WHERE %s";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM client c WHERE c.domain_id = ?";
    private static final String INSERT_SQL = "INSERT INTO client " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "accessible_, " +
        "domain_id, " +
        "description, " +
        "name, " +
        "path, " +
        "project_id, " +
        "role_id, " +
        "secret, " +
        "access_token_validity_seconds, " +
        "refresh_token_validity_seconds, " +
        "external_url" +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_REDIRECT_URL_SQL = "INSERT INTO client_redirect_url_map " +
        "(" +
        "id, " +
        "redirect_url" +
        ") VALUES " +
        "(?,?)";

    private static final String INSERT_GRANT_TYPE_SQL = "INSERT INTO client_grant_type_map " +
        "(" +
        "id, " +
        "grant_type" +
        ") VALUES " +
        "(?,?)";
    private static final String INSERT_CLIENT_TYPE_SQL = "INSERT INTO client_type_map " +
        "(" +
        "id, " +
        "type" +
        ") VALUES " +
        "(?,?)";
    private static final String INSERT_EXT_RESOURCE_SQL = "INSERT INTO external_resources_map " +
        "(" +
        "id, " +
        "domain_id" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM client c WHERE c.id = ?";
    private static final String BATCH_DELETE_BY_ID_SQL = "DELETE FROM client c WHERE c.id IN (%s)";
    private static final String BATCH_DELETE_RESOURCE_BY_ID_SQL =
        "DELETE FROM resources_map rm WHERE rm.id IN (%s)";
    private static final String BATCH_DELETE_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM resources_map rm WHERE rm.id = ? AND rm.domain_id IN (%s)";
    private static final String DELETE_EXT_RESOURCE_BY_ID_SQL =
        "DELETE FROM external_resources_map erm WHERE erm.id = ?";
    private static final String BATCH_DELETE_EXT_RESOURCE_BY_ID_SQL =
        "DELETE FROM external_resources_map erm WHERE erm.id IN (%s)";
    private static final String BATCH_DELETE_EXT_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM external_resources_map erm WHERE erm.id = ? AND rm.domain_id IN (%s)";
    private static final String DELETE_CLIENT_TYPE_BY_ID_SQL =
        "DELETE FROM client_type_map ctm WHERE ctm.id = ?";
    private static final String BATCH_DELETE_CLIENT_TYPE_BY_ID_AND_TYPE_SQL =
        "DELETE FROM client_type_map ctm WHERE ctm.id = ? AND ctm.type IN (%s)";
    private static final String BATCH_DELETE_CLIENT_TYPE_BY_ID_SQL =
        "DELETE FROM client_type_map ctm WHERE ctm.id IN (%s)";
    private static final String DELETE_CLIENT_GRANT_TYPE_BY_ID_SQL =
        "DELETE FROM client_grant_type_map cgtm WHERE cgtm.id = ?";
    private static final String BATCH_DELETE_CLIENT_GRANT_TYPE_BY_ID_SQL =
        "DELETE FROM client_grant_type_map cgtm WHERE cgtm.id IN (%s)";
    private static final String BATCH_DELETE_CLIENT_GRANT_TYPE_BY_ID_AND_TYPE_SQL =
        "DELETE FROM client_grant_type_map cgtm WHERE cgtm.id = ? AND cgtm.grant_type IN (%s)";
    private static final String DELETE_REDIRECT_URL_BY_ID_SQL =
        "DELETE FROM client_redirect_url_map crum WHERE crum.id = ?";
    private static final String BATCH_DELETE_REDIRECT_URL_BY_ID_AND_URL_SQL =
        "DELETE FROM client_redirect_url_map crum WHERE crum.id = ? AND crum.redirect_url IN (%s)";
    private static final String BATCH_DELETE_REDIRECT_URL_BY_ID_SQL =
        "DELETE FROM client_redirect_url_map crum WHERE crum.id IN (%s)";
    private static final String FIND_ALL_PROJECT_ID_SQL =
        "SELECT DISTINCT c.project_id FROM client c";
    private static final String FIND_ALL_CLIENT_ID_SQL = "SELECT c.domain_id FROM client c";
    private static final String COUNT_TOTAL_SQL = "SELECT COUNT(*) AS count FROM client";
    private static final String COUNT_PROJECT_TOTAL_SQL =
        "SELECT COUNT(*) AS count FROM client c WHERE c.project_id = ?";
    private static final String COUNT_RESOURCE_SQL =
        "SELECT COUNT(DISTINCT rm.id) AS count FROM resources_map rm LEFT JOIN client c ON rm.id = c.id WHERE rm.domain_id IN (%s)";
    private static final String FIND_EXT_RESOURCE_IDS_BY_ID_SQL = "SELECT m.domain_id " +
        "FROM external_resources_map m WHERE m.id = ?";
    private static final String FIND_GRANT_TYPE_BY_ID_SQL = "SELECT m.grant_type " +
        "FROM client_grant_type_map m WHERE m.id = ?";
    private static final String FIND_CLIENT_TYPE_BY_ID_SQL = "SELECT m.type " +
        "FROM client_type_map m WHERE m.id = ?";
    private static final String FIND_REDIRECT_URL_BY_ID_SQL = "SELECT m.redirect_url " +
        "FROM client_redirect_url_map m WHERE m.id = ?";
    private static final String UPDATE_SQL = "UPDATE client c SET " +
        "c.modified_at = ? ," +
        "c.modified_by = ?, " +
        "c.version = ?, " +
        "c.accessible_ = ?, " +
        "c.description = ?, " +
        "c.name = ?, " +
        "c.path = ?, " +
        "c.secret = ?, " +
        "c.access_token_validity_seconds = ?, " +
        "c.refresh_token_validity_seconds = ?, " +
        "c.external_url = ? " +
        "WHERE c.id = ? AND c.version = ? ";


    @Override
    public Client query(ClientId clientId) {
        List<Client> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                clientId.getDomainId()
            );
        return data.isEmpty() ? null : data.get(0);
    }

    @Override
    public void add(Client client) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                client.getId(),
                client.getCreatedAt(),
                client.getCreatedBy(),
                client.getModifiedAt(),
                client.getModifiedBy(),
                0,
                client.getAccessible(),
                client.getClientId().getDomainId(),
                client.getDescription(),
                client.getName(),
                client.getPath(),
                client.getProjectId().getDomainId(),
                client.getRoleId().getDomainId(),
                client.getSecret(),
                client.getTokenDetail().getAccessTokenValiditySeconds(),
                client.getTokenDetail().getRefreshTokenValiditySeconds(),
                Utility.notNull(client.getExternalUrl()) ? client.getExternalUrl().getValue() : null
            );
        //for linked tables
        if (Utility.notNullOrEmpty(client.getRedirectUrls())) {
            List<BatchInsertKeyValue> keyValues = new ArrayList<>();
            List<BatchInsertKeyValue> collect =
                client.getRedirectUrls().stream()
                    .map(ee -> new BatchInsertKeyValue(client.getId(), ee.getValue())).collect(
                        Collectors.toList());
            keyValues.addAll(collect);
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_REDIRECT_URL_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (Utility.notNullOrEmpty(client.getExternalResources())) {
            List<BatchInsertKeyValue> keyValues = new ArrayList<>();
            List<BatchInsertKeyValue> collect =
                client.getExternalResources().stream()
                    .map(ee -> new BatchInsertKeyValue(client.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
            keyValues.addAll(collect);
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_EXT_RESOURCE_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (Utility.notNullOrEmpty(client.getTypes())) {
            List<BatchInsertKeyValue> keyValues = new ArrayList<>();
            List<BatchInsertKeyValue> collect =
                client.getTypes().stream()
                    .map(ee -> new BatchInsertKeyValue(client.getId(), ee.name())).collect(
                        Collectors.toList());
            keyValues.addAll(collect);
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_CLIENT_TYPE_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (Utility.notNullOrEmpty(client.getGrantTypes())) {
            List<BatchInsertKeyValue> keyValues = new ArrayList<>();
            List<BatchInsertKeyValue> collect =
                client.getGrantTypes().stream()
                    .map(ee -> new BatchInsertKeyValue(client.getId(), ee.name())).collect(
                        Collectors.toList());
            keyValues.addAll(collect);
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_GRANT_TYPE_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void remove(Client client) {
        if (Utility.notNullOrEmpty(client.getExternalResources())) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_EXT_RESOURCE_BY_ID_SQL,
                    client.getId()
                );
        }
        if (Utility.notNullOrEmpty(client.getGrantTypes())) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_CLIENT_GRANT_TYPE_BY_ID_SQL,
                    client.getId()
                );
        }
        if (Utility.notNullOrEmpty(client.getTypes())) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_CLIENT_TYPE_BY_ID_SQL,
                    client.getId()
                );
        }
        if (Utility.notNullOrEmpty(client.getRedirectUrls())) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_REDIRECT_URL_BY_ID_SQL,
                    client.getId()
                );
        }
        CommonDomainRegistry.getJdbcTemplate()
            .update(DELETE_BY_ID_SQL,
                client.getId()
            );
    }

    @Override
    public void remove(Collection<Client> clients) {
        Set<Long> ids = clients.stream().map(Auditable::getId).collect(Collectors.toSet());
        String inClause = DatabaseUtility.getInClause(ids.size());
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_RESOURCE_BY_ID_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_EXT_RESOURCE_BY_ID_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_CLIENT_TYPE_BY_ID_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_CLIENT_GRANT_TYPE_BY_ID_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_REDIRECT_URL_BY_ID_SQL, inClause),
                ids.toArray()
            );
        CommonDomainRegistry.getJdbcTemplate()
            .update(BATCH_DELETE_BY_ID_SQL,
                ids.toArray()
            );
    }

    @Override
    public SumPagedRep<Client> query(ClientQuery query) {
        if (query.getResources() != null) {
            return resourceSearch(query);
        }
        List<String> whereClause = new ArrayList<>();
        if (Utility.notNullOrEmpty(query.getClientIds())) {
            String inClause = DatabaseUtility.getInClause(query.getClientIds().size());
            String byDomainIds = String.format("c.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Utility.notNull(query.getResourceFlag())) {
            String accessible =
                query.getResourceFlag() ? "c.accessible_ = 1" : "c.accessible_ = 0";
            whereClause.add(accessible);
        }
        if (Utility.notNull(query.getName())) {
            String name = "c.name LIKE ?";
            whereClause.add(name);
        }
        if (Utility.notNullOrEmpty(query.getProjectIds())) {
            String inClause = DatabaseUtility.getInClause(query.getProjectIds().size());
            String byDomainIds = String.format("c.project_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
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
        if (Utility.notNullOrEmpty(query.getClientIds())) {
            args.addAll(
                query.getClientIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }
        if (Utility.notNull(query.getName())) {
            args.add("%" + query.getName() + "%");
        }
        if (Utility.notNullOrEmpty(query.getProjectIds())) {
            args.addAll(
                query.getProjectIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
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
        List<Client> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public Set<ProjectId> getProjectIds() {
        List<ProjectId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_PROJECT_ID_SQL,
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
    public Set<ClientId> allClientIds() {
        List<ClientId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_ALL_CLIENT_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<ClientId> list = new ArrayList<>();
                    do {
                        list.add(new ClientId(rs.getString("domain_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public long countTotal() {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_TOTAL_SQL,
                new DatabaseUtility.ExtractCount()
            );
        return query;
    }

    @Override
    public long countProjectTotal(ProjectId projectId) {
        Long query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_PROJECT_TOTAL_SQL,
                new DatabaseUtility.ExtractCount(),
                projectId.getDomainId()
            );
        return query;
    }

    @Override
    public Set<ClientId> getExternalResources(Long id) {
        List<ClientId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_EXT_RESOURCE_IDS_BY_ID_SQL,
                new ClientIdRowMapper(),
                id
            );
        return new HashSet<>(data);
    }

    @Override
    public void updateExternalResources(Long id, Set<ClientId> old, Set<ClientId> updated){
        DatabaseUtility.updateMap(old, updated,
            (added) -> {
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(id, ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_EXT_RESOURCE_SQL, insertKeyValues,
                        insertKeyValues.size(),
                        (ps, perm) -> {
                            ps.setLong(1, perm.getId());
                            ps.setString(2, perm.getValue());
                        });
            }, (removed) -> {
                String inClause = DatabaseUtility.getInClause(removed.size());
                List<Object> args = new ArrayList<>();
                args.add(id);
                args.addAll(
                    removed.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_EXT_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL,
                            inClause),
                        args.toArray()
                    );
            });
    }

    @Override
    public Set<GrantType> getGrantType(Long id) {
        List<GrantType> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_GRANT_TYPE_BY_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<GrantType> list = new ArrayList<>();
                    do {
                        list.add(GrantType.valueOf(rs.getString("grant_type")));
                    } while (rs.next());
                    return list;
                },
                id
            );
        return new HashSet<>(data);
    }

    @Override
    public Set<ClientType> getType(Long id) {
        List<ClientType> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_CLIENT_TYPE_BY_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<ClientType> list = new ArrayList<>();
                    do {
                        list.add(ClientType.valueOf(rs.getString("type")));
                    } while (rs.next());
                    return list;
                },
                id
            );
        return new HashSet<>(data);
    }

    @Override
    public Set<RedirectUrl> getRedirectUrls(Long id) {
        List<RedirectUrl> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_REDIRECT_URL_BY_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<RedirectUrl> list = new ArrayList<>();
                    do {
                        list.add(new RedirectUrl(rs.getString("redirect_url")));
                    } while (rs.next());
                    return list;
                },
                id
            );
        return new HashSet<>(data);
    }

    @Override
    public void update(Client old, Client updated) {
        if (old.sameAs(updated)) {
            return;
        }
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getModifiedAt(),
                updated.getModifiedBy(),
                updated.getVersion() + 1,
                updated.getAccessible(),
                updated.getDescription(),
                updated.getName(),
                updated.getPath(),
                updated.getSecret(),
                updated.getTokenDetail().getAccessTokenValiditySeconds(),
                updated.getTokenDetail().getRefreshTokenValiditySeconds(),
                Utility.isNull(updated.getExternalUrl()) ? null :
                    updated.getExternalUrl().getValue(),
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(update);
        DatabaseUtility.updateMap(old.getGrantTypes(),
            updated.getGrantTypes(),
            (added) -> {
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.name()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_GRANT_TYPE_SQL, insertKeyValues,
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
                    removed.stream().map(Enum::name).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_CLIENT_GRANT_TYPE_BY_ID_AND_TYPE_SQL,
                            inClause),
                        args.toArray()
                    );
            });
        Set<RedirectUrl> oldRedirectUrls;
        Set<RedirectUrl> updatedRedirectUrls;
        oldRedirectUrls = old.getRedirectUrls();
        updatedRedirectUrls = updated.getRedirectUrls();
        DatabaseUtility.updateMap(oldRedirectUrls,
            updatedRedirectUrls,
            (added) -> {
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getValue()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_REDIRECT_URL_SQL, insertKeyValues,
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
                    removed.stream().map(RedirectUrl::getValue).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_REDIRECT_URL_BY_ID_AND_URL_SQL,
                            inClause),
                        args.toArray()
                    );
            });
        DatabaseUtility.updateMap(old.getTypes(), updated.getTypes(),
            (added) -> {
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.name()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_CLIENT_TYPE_SQL, insertKeyValues,
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
                    removed.stream().map(Enum::name).collect(Collectors.toSet()));
                CommonDomainRegistry.getJdbcTemplate()
                    .update(
                        String.format(BATCH_DELETE_CLIENT_TYPE_BY_ID_AND_TYPE_SQL,
                            inClause),
                        args.toArray()
                    );
            });
        DatabaseUtility.updateMap(old.getExternalResources(), updated.getExternalResources(),
            (added) -> {
                List<BatchInsertKeyValue> insertKeyValues = new ArrayList<>();
                List<BatchInsertKeyValue> collect = added.stream()
                    .map(ee -> new BatchInsertKeyValue(old.getId(), ee.getDomainId()))
                    .collect(
                        Collectors.toList());
                insertKeyValues.addAll(collect);
                CommonDomainRegistry.getJdbcTemplate()
                    .batchUpdate(INSERT_EXT_RESOURCE_SQL, insertKeyValues,
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
                        String.format(BATCH_DELETE_EXT_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL,
                            inClause),
                        args.toArray()
                    );
            });
    }

    private SumPagedRep<Client> resourceSearch(ClientQuery query) {
        Set<String> resourceIds =
            query.getResources().stream().map(DomainId::getDomainId).collect(
                Collectors.toSet());
        Set<String> projectIds;
        if (query.getProjectIds() != null) {
            projectIds =
                query.getProjectIds().stream().map(DomainId::getDomainId).collect(
                    Collectors.toSet());
        } else {
            projectIds = Collections.emptySet();
        }
        return commonOrMappingSearch(resourceIds,
            query.getPageConfig(), projectIds);
    }

    private SumPagedRep<Client> commonOrMappingSearch(Set<String> mapping,
                                                      PageConfig pageConfig,
                                                      Set<String> projectIds) {
        List<Object> args = new ArrayList<>();
        args.addAll(mapping);
        String finalCountQuery = COUNT_RESOURCE_SQL +
            (projectIds.isEmpty() ? "" : " AND c.project_id IN (%s)");
        String inClause = DatabaseUtility.getInClause(mapping.size());
        String countFormat = String.format(finalCountQuery, inClause);
        String countResult = countFormat;
        if (!projectIds.isEmpty()) {
            String inClause2 = DatabaseUtility.getInClause(projectIds.size());
            countResult = String.format(countFormat, inClause2);
            args.addAll(projectIds);
        }
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                countResult,
                new DatabaseUtility.ExtractCount(),
                args.toArray()
            );
        String finalDataQuery = RESOURCE_SQL +
            (projectIds.isEmpty() ? "" : " AND c.project_id IN (%s)") +
            " GROUP BY mt.id LIMIT ? OFFSET ?";
        String dataFormat = String.format(finalDataQuery, inClause);
        String dataResult = dataFormat;
        if (!projectIds.isEmpty()) {
            String inClause2 = DatabaseUtility.getInClause(projectIds.size());
            dataResult = String.format(dataFormat, inClause2);
        }
        args.add(pageConfig.getPageSize());
        args.add(pageConfig.getOffset());
        List<Client> data = CommonDomainRegistry.getJdbcTemplate()
            .query(dataResult,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    private static class RowMapper implements ResultSetExtractor<List<Client>> {

        @Override
        public List<Client> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Client> list = new ArrayList<>();
            long currentId = -1L;
            Client client = null;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    client = Client.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        DatabaseUtility.getNullableBoolean(rs, "accessible_"),
                        new ClientId(rs.getString("domain_id")),
                        rs.getString("description"),
                        rs.getString("name"),
                        rs.getString("path"),
                        new ProjectId(rs.getString("project_id")),
                        new RoleId(rs.getString("role_id")),
                        rs.getString("secret"),
                        DatabaseUtility.getNullableInteger(rs, "access_token_validity_seconds"),
                        DatabaseUtility.getNullableInteger(rs, "refresh_token_validity_seconds"),
                        Utility.notNull(rs.getString("external_url")) ?
                            new ExternalUrl(rs.getString("external_url")) : null
                    );
                    list.add(client);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }

}
