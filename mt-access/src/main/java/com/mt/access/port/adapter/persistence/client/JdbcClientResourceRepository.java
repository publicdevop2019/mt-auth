package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientResourceRepository;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcClientResourceRepository implements ClientResourceRepository {
    private static final String FIND_RESOURCE_IDS_BY_ID_SQL = "SELECT m.domain_id " +
        "FROM resources_map m WHERE m.id = ?";
    private static final String INSERT_RESOURCE_SQL = "INSERT INTO resources_map " +
        "(" +
        "id, " +
        "domain_id" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_RESOURCE_BY_ID_SQL =
        "DELETE FROM resources_map rm WHERE rm.id = ?";
    private static final String BATCH_DELETE_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM resources_map rm WHERE rm.id = ? AND rm.domain_id IN (%s)";
    private static final String DELETE_RESOURCE_BY_DOMAIN_ID_SQL =
        "DELETE FROM resources_map rm WHERE rm.domain_id = ?";

    @Override
    public Set<ClientId> query(Client client) {
        List<ClientId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_RESOURCE_IDS_BY_ID_SQL,
                new ClientIdRowMapper(),
                client.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void add(Client client, Set<ClientId> resources) {
        if (Utility.notNullOrEmpty(resources)) {
            List<BatchInsertKeyValue> keyValues = Utility.mapToList(resources,
                ee -> new BatchInsertKeyValue(client.getId(), ee.getDomainId()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_RESOURCE_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void remove(Client client, Set<ClientId> resources) {
        if (Utility.notNullOrEmpty(resources)) {
            String inClause = DatabaseUtility.getInClause(resources.size());
            List<Object> args = new ArrayList<>();
            args.add(client.getId());
            args.addAll(Utility.mapToSet(resources, DomainId::getDomainId));
            CommonDomainRegistry.getJdbcTemplate()
                .update(
                    String.format(BATCH_DELETE_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL,
                        inClause),
                    args.toArray()
                );
        }
    }

    @Override
    public void removeRef(ClientId removedClientId) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                DELETE_RESOURCE_BY_DOMAIN_ID_SQL,
                removedClientId.getDomainId()
            );
    }

    @Override
    public void removeAll(Client client, Set<ClientId> existingResources) {
        if (Utility.notNullOrEmpty(existingResources)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_RESOURCE_BY_ID_SQL,
                    client.getId()
                );
        }
    }
}
