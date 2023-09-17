package com.mt.access.port.adapter.persistence.image;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.image.ImageRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcImageRepository implements ImageRepository {

    private static final String INSERT_SQL = "INSERT INTO image " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "content_type, " +
        "domain_id, " +
        "original_name, " +
        "source" +
        ") VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_DOMAIN_ID_SQL =
        "SELECT * FROM image i WHERE i.domain_id = ?";

    @Override
    public Optional<Image> query(ImageId id) {
        List<Image> query = CommonDomainRegistry.getJdbcTemplate()
            .query(
                FIND_BY_DOMAIN_ID_SQL,
                new RowMapper(),
                id.getDomainId()
            );
        return query.isEmpty() ? Optional.empty() : Optional.ofNullable(query.get(0));
    }

    @Override
    public void add(Image image) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                image.getId(),
                Instant.now().toEpochMilli(),
                DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
                image.getContentType(),
                image.getImageId().getDomainId(),
                image.getOriginalName(),
                image.getSource()
            );
    }

    private static class RowMapper implements ResultSetExtractor<List<Image>> {

        @Override
        public List<Image> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<Image> results = new ArrayList<>();
            long currentId = -1L;
            Image result;
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    result = Image.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        rs.getString("content_type"),
                        new ImageId(rs.getString("domain_id")),
                        rs.getString("original_name"),
                        rs.getBytes("source")
                    );
                    results.add(result);
                    currentId = dbId;
                }
            } while (rs.next());
            return results;
        }
    }
}
