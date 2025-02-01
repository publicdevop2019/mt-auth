package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientRedirectUrlRepository;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.infrastructure.Utility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcClientRedirectUrlRepository implements ClientRedirectUrlRepository {
    private static final String FIND_REDIRECT_URL_BY_ID_SQL = "SELECT m.redirect_url " +
        "FROM client_redirect_url_map m WHERE m.id = ?";
    private static final String INSERT_REDIRECT_URL_SQL = "INSERT INTO client_redirect_url_map " +
        "(" +
        "id, " +
        "redirect_url" +
        ") VALUES " +
        "(?,?)";
    private static final String DELETE_REDIRECT_URL_BY_ID_SQL =
        "DELETE FROM client_redirect_url_map crum WHERE crum.id = ?";
    private static final String BATCH_DELETE_REDIRECT_URL_BY_ID_AND_URL_SQL =
        "DELETE FROM client_redirect_url_map crum WHERE crum.id = ? AND crum.redirect_url IN (%s)";

    @Override
    public Set<RedirectUrl> query(Client client) {
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
                client.getId()
            );
        return new HashSet<>(data);
    }

    @Override
    public void remove(Client client, Set<RedirectUrl> redirectUrls) {
        String inClause = DatabaseUtility.getInClause(redirectUrls.size());
        List<Object> args = new ArrayList<>();
        args.add(client.getId());
        args.addAll(Utility.mapToSet(redirectUrls,RedirectUrl::getValue));
        CommonDomainRegistry.getJdbcTemplate()
            .update(
                String.format(BATCH_DELETE_REDIRECT_URL_BY_ID_AND_URL_SQL,
                    inClause),
                args.toArray()
            );
    }

    @Override
    public void add(Client client, Set<RedirectUrl> redirectUrls) {
        if (Checker.notNullOrEmpty(redirectUrls)) {
            List<BatchInsertKeyValue> keyValues = Utility.mapToList(redirectUrls,
                ee -> new BatchInsertKeyValue(client.getId(), ee.getValue()));
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate(INSERT_REDIRECT_URL_SQL, keyValues,
                    keyValues.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    @Override
    public void removeAll(Client client, Set<RedirectUrl> redirectUrls) {
        if (Checker.notNullOrEmpty(redirectUrls)) {
            CommonDomainRegistry.getJdbcTemplate()
                .update(DELETE_REDIRECT_URL_BY_ID_SQL,
                    client.getId()
                );
        }
    }
}
