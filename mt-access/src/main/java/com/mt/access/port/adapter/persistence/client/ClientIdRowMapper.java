package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ClientIdRowMapper implements ResultSetExtractor<List<ClientId>> {

    @Override
    public List<ClientId> extractData(ResultSet rs)
        throws SQLException, DataAccessException {
        if (!rs.next()) {
            return Collections.emptyList();
        }
        List<ClientId> list = new ArrayList<>();
        do {
            String idRaw = rs.getString("domain_id");
            if (Checker.notNull(idRaw)) {
                list.add(new ClientId(idRaw));
            }
        } while (rs.next());
        return list;
    }
}
