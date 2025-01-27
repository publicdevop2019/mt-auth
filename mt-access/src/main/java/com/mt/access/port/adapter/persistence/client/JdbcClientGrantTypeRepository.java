package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientGrantTypeRepository;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcClientGrantTypeRepository implements ClientGrantTypeRepository {
    private static final String FIND_GRANT_TYPE_BY_ID_SQL = "SELECT m.grant_type " +
        "FROM client_grant_type_map m WHERE m.id = ?";
    private static final String INSERT_GRANT_TYPE_SQL = "INSERT INTO client_grant_type_map " +
        "(" +
        "id, " +
        "grant_type" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_CLIENT_GRANT_TYPE_BY_ID_SQL =
        "DELETE FROM client_grant_type_map cgtm WHERE cgtm.id = ?";
    private static final String BATCH_DELETE_CLIENT_GRANT_TYPE_BY_ID_AND_TYPE_SQL =
        "DELETE FROM client_grant_type_map cgtm WHERE cgtm.id = ? AND cgtm.grant_type IN (%s)";

    @Override
    public Set<GrantType> query(Client client) {
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
                client.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void remove(Client client, Set<GrantType> removed) {
        String inClause = DatabaseUtility.getInClause(removed.size());
        List<Object> args = new ArrayList<>();
        args.add(client.getId());
        args.addAll(Utility.mapToSet(removed, Enum::name));
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_CLIENT_GRANT_TYPE_BY_ID_AND_TYPE_SQL,
                    inClause),
                args.toArray()
            );
    }

    @Override
    public void add(Client client, Set<GrantType> added) {
        List<BatchInsertKeyValue> keyValues = Utility.mapToList(added,
            e -> new BatchInsertKeyValue(client.getId(), e.name()));
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate(INSERT_GRANT_TYPE_SQL, keyValues,
                keyValues.size(),
                (ps, perm) -> {
                    ps.setLong(1, perm.getId());
                    ps.setString(2, perm.getValue());
                });
    }

    @Override
    public void removeAll(Client client, Set<GrantType> grantTypes) {
        if (Utility.notNullOrEmpty(grantTypes)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_CLIENT_GRANT_TYPE_BY_ID_SQL,
                    client.getId()
                );
        }
    }
}
