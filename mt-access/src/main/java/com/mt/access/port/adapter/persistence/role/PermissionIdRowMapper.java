package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

class PermissionIdRowMapper implements ResultSetExtractor<List<PermissionId>> {

    @Override
    public List<PermissionId> extractData(ResultSet rs)
        throws SQLException, DataAccessException {
        if (!rs.next()) {
            return Collections.emptyList();
        }
        List<PermissionId> list = new ArrayList<>();
        do {
            String idRaw = rs.getString("permission");
            if (Checker.notNull(idRaw)) {
                list.add(new PermissionId(idRaw));
            }
        } while (rs.next());
        return list;
    }
}
