package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientExternalResourceRepository;
import com.mt.access.domain.model.client.ClientId;
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
public class JdbcClientExternalResourceRepository implements ClientExternalResourceRepository {
    private static final String FIND_EXT_RESOURCE_IDS_BY_ID_SQL = "SELECT m.domain_id " +
        "FROM external_resources_map m WHERE m.id = ?";
    private static final String INSERT_EXT_RESOURCE_SQL = "INSERT INTO external_resources_map " +
        "(" +
        "id, " +
        "domain_id" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_EXT_RESOURCE_BY_ID_SQL =
        "DELETE FROM external_resources_map erm WHERE erm.id = ?";
    private static final String BATCH_DELETE_EXT_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL =
        "DELETE FROM external_resources_map erm WHERE erm.id = ? AND erm.domain_id IN (%s)";

    @Override
    public Set<ClientId> query(Client client) {
        List<ClientId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(FIND_EXT_RESOURCE_IDS_BY_ID_SQL,
                new ClientIdRowMapper(),
                client.getId()
            );
        return new HashSet<>(data);
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
                    String.format(BATCH_DELETE_EXT_RESOURCE_BY_ID_AND_DOMAIN_ID_SQL,
                        inClause),
                    args.toArray()
                );
        }
    }

    @Override
    public void add(Client client, Set<ClientId> resources) {
        if (Utility.notNullOrEmpty(resources)) {
            List<BatchInsertKeyValue> keyValues = Utility.mapToList(resources,
                ee -> new BatchInsertKeyValue(client.getId(), ee.getDomainId()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_EXT_RESOURCE_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void removeAll(Client client, Set<ClientId> resources) {
        if (Utility.notNullOrEmpty(resources)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_EXT_RESOURCE_BY_ID_SQL,
                    client.getId()
                );
        }
    }
}
