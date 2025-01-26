package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.user.Language;
import com.mt.access.domain.model.user.LoginUser;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserAvatar;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserName;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.access.domain.model.user.UserRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRepository implements UserRepository {

    public static final String QUERY_USER_BY_EMAIL =
        "SELECT * FROM user_ u WHERE u.email = ?";
    public static final String QUERY_USER_BY_MOBILE =
        "SELECT * FROM user_ u WHERE u.country_code = ? AND u.mobile_number = ?";
    private static final String DYNAMIC_DATA_QUERY_SQL = "SELECT * FROM user_ u " +
        "WHERE %s ORDER BY u.id ASC LIMIT ? OFFSET ? ";
    private static final String DYNAMIC_COUNT_QUERY_SQL = "SELECT COUNT(*) AS count FROM user_ u " +
        "WHERE %s";
    private static final String COUNT_TOTAL = "SELECT COUNT(*) AS count FROM user_";
    private static final String GET_USER_ID_SQL =
        "SELECT u.domain_id FROM user_ u WHERE u.email = ?";
    private static final String GET_USER_IDS_SQL = "SELECT u.domain_id FROM user_ u";
    private static final String INSERT_SQL = "INSERT INTO user_ " +
        "(" +
        "id, " +
        "created_at, " +
        "created_by, " +
        "modified_at, " +
        "modified_by, " +
        "version, " +
        "email, " +
        "locked, " +
        "password, " +
        "domain_id, " +
        "username, " +
        "country_code, " +
        "mobile_number, " +
        "avatar_link, " +
        "language " +
        ") VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String QUERY_LOGIN_USER_BY_EMAIL_SQL =
        "SELECT u.domain_id, u.password, u.locked FROM user_ u WHERE u.email = ?";
    private static final String QUERY_LOGIN_USER_BY_ID_SQL =
        "SELECT u.domain_id, u.password, u.locked FROM user_ u WHERE u.domain_id = ?";
    private static final String GET_USER_ID_SQL_BY_MOBILE =
        "SELECT u.domain_id FROM user_ u WHERE u.country_code = ? AND u.mobile_number = ?";
    private static final String GET_USER_ID_SQL_BY_USER_NAME =
        "SELECT u.domain_id FROM user_ u WHERE u.username = ?";
    private static final String UPDATE_SQL = "UPDATE user_ u SET " +
        "u.modified_at = ? ," +
        "u.modified_by = ?, " +
        "u.locked = ?, " +
        "u.password = ?, " +
        "u.username = ?, " +
        "u.country_code = ?, " +
        "u.mobile_number = ?, " +
        "u.email = ?, " +
        "u.avatar_link = ?, " +
        "u.language = ?, " +
        "u.version = ? " +
        "WHERE u.id = ? AND u.version = ? ";

    @Override
    public Optional<User> query(UserId userId) {
        return query(new UserQuery(userId)).findFirst();
    }

    @Override
    public Optional<UserId> queryUserId(UserEmail email) {
        UserId query = CommonDomainRegistry.getJdbcTemplate()
            .query(GET_USER_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    return new UserId(rs.getString("domain_id"));
                },
                email.getEmail()
            );
        return Optional.ofNullable(query);
    }

    @Override
    public Optional<User> query(UserEmail email) {
        List<User> data = CommonDomainRegistry.getJdbcTemplate()
            .query(QUERY_USER_BY_EMAIL,
                new RowMapper(),
                email.getEmail()
            );
        return data.isEmpty() ? Optional.empty() : Optional.of(data.get(0));
    }

    @Override
    public Optional<User> query(UserMobile mobile) {
        List<User> data = CommonDomainRegistry.getJdbcTemplate()
            .query(QUERY_USER_BY_MOBILE,
                new RowMapper(),
                mobile.getCountryCode(),
                mobile.getMobileNumber()
            );
        return data.isEmpty() ? Optional.empty() : Optional.of(data.get(0));
    }

    @Override
    public void add(User user) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                user.getId(),
                user.getCreatedAt(),
                user.getCreatedBy(),
                user.getModifiedAt(),
                user.getModifiedBy(),
                0,
                Utility.isNull(user.getEmail()) ? null : user.getEmail().getEmail(),
                user.getLocked(),
                Utility.isNull(user.getPassword()) ? null : user.getPassword().getPassword(),
                user.getUserId().getDomainId(),
                Utility.isNull(user.getUserName()) ? null : user.getUserName().getValue(),
                Utility.isNull(user.getMobile()) ? null : user.getMobile().getCountryCode(),
                Utility.isNull(user.getMobile()) ? null : user.getMobile().getMobileNumber(),
                Utility.isNull(user.getUserAvatar()) ? null : user.getUserAvatar().getValue(),
                Utility.isNull(user.getLanguage()) ? null : user.getLanguage().name()
            );
    }

    @Override
    public SumPagedRep<User> query(UserQuery query) {
        List<String> whereClause = new ArrayList<>();
        if (Utility.notNullOrEmpty(query.getUserIds())) {
            String inClause = DatabaseUtility.getInClause(query.getUserIds().size());
            String byDomainIds = String.format("u.domain_id IN (%s)", inClause);
            whereClause.add(byDomainIds);
        }
        if (Utility.notNullOrEmpty(query.getUserEmailKeys())) {
            List<String> orClause = new ArrayList<>();
            query.getUserEmailKeys().forEach(e -> {
                String email = "u.email LIKE ?";
                orClause.add(email);
            });
            String join = String.join(" OR ", orClause);
            whereClause.add(join);
        }
        if (Utility.notNullOrEmpty(query.getUserMobileKeys())) {
            List<String> orClause = new ArrayList<>();
            query.getUserMobileKeys().forEach(e -> {
                String mobile = "u.mobile_number LIKE ?";
                orClause.add(mobile);
            });
            String join = String.join(" OR ", orClause);
            whereClause.add(join);
        }
        if (Utility.notNullOrEmpty(query.getUsernameKeys())) {
            List<String> orClause = new ArrayList<>();
            query.getUsernameKeys().forEach(e -> {
                String username = "u.username LIKE ?";
                orClause.add(username);
            });
            String join = String.join(" OR ", orClause);
            whereClause.add(join);
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
        if (Utility.notNullOrEmpty(query.getUserIds())) {
            args.addAll(
                query.getUserIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        }
        if (Utility.notNull(query.getUserEmailKeys())) {
            args.addAll(
                query.getUserEmailKeys().stream().map(e -> e + "%").collect(Collectors.toSet()));
        }
        if (Utility.notNull(query.getUserMobileKeys())) {
            args.addAll(
                query.getUserMobileKeys().stream().map(e -> e + "%").collect(Collectors.toSet()));
        }
        if (Utility.notNull(query.getUsernameKeys())) {
            args.addAll(
                query.getUsernameKeys().stream().map(e -> e + "%").collect(Collectors.toSet()));
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

        List<User> data = CommonDomainRegistry.getJdbcTemplate()
            .query(finalDataQuery,
                new RowMapper(),
                args.toArray()
            );
        return new SumPagedRep<>(data, count);
    }

    @Override
    public long countTotal() {
        Long count = CommonDomainRegistry.getJdbcTemplate()
            .query(
                COUNT_TOTAL,
                new DatabaseUtility.ExtractCount()
            );
        return count;
    }

    @Override
    public Set<UserId> getIds() {
        List<UserId> data = CommonDomainRegistry.getJdbcTemplate()
            .query(GET_USER_IDS_SQL,
                rs -> {
                    if (!rs.next()) {
                        return Collections.emptyList();
                    }
                    List<UserId> list = new ArrayList<>();
                    do {
                        list.add(new UserId(rs.getString("domain_id")));
                    } while (rs.next());
                    return list;
                }
            );
        return new HashSet<>(data);
    }

    @Override
    public Optional<LoginUser> queryLoginUser(UserEmail email) {
        LoginUser query = CommonDomainRegistry.getJdbcTemplate()
            .query(QUERY_LOGIN_USER_BY_EMAIL_SQL,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    UserPassword userPassword = new UserPassword();
                    userPassword.setPassword(rs.getString("password"));
                    return LoginUser.deserialize(new UserId(rs.getString("domain_id")),
                        rs.getBoolean("locked")
                        , userPassword);
                },
                email.getEmail()
            );
        return Optional.ofNullable(query);
    }

    @Override
    public Optional<LoginUser> queryLoginUser(UserId userId) {
        LoginUser query = CommonDomainRegistry.getJdbcTemplate()
            .query(QUERY_LOGIN_USER_BY_ID_SQL,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    UserPassword userPassword = new UserPassword();
                    userPassword.setPassword(rs.getString("password"));
                    return LoginUser.deserialize(new UserId(rs.getString("domain_id")),
                        rs.getBoolean("locked")
                        , userPassword);
                },
                userId.getDomainId()
            );
        return Optional.ofNullable(query);
    }

    @Override
    public void update(User old, User updated) {
        if (old.sameAs(updated)) {
            return;
        }
        int count = CommonDomainRegistry.getJdbcTemplate()
            .update(UPDATE_SQL,
                updated.getModifiedAt(),
                updated.getModifiedBy(),
                updated.getLocked(),
                Utility.isNull(updated.getPassword()) ? null : updated.getPassword().getPassword(),
                Utility.isNull(updated.getUserName()) ? null : updated.getUserName().getValue(),
                Utility.isNull(updated.getMobile()) ? null : updated.getMobile().getCountryCode(),
                Utility.isNull(updated.getMobile()) ? null : updated.getMobile().getMobileNumber(),
                Utility.isNull(updated.getEmail()) ? null : updated.getEmail().getEmail(),
                Utility.isNull(updated.getUserAvatar()) ? null : updated.getUserAvatar().getValue(),
                Utility.isNull(updated.getLanguage()) ? null : updated.getLanguage().name(),
                updated.getVersion() + 1,
                updated.getId(),
                updated.getVersion()
            );
        DatabaseUtility.checkUpdate(count);
    }

    @Override
    public Optional<UserId> queryUserId(UserMobile userMobile) {
        UserId query = CommonDomainRegistry.getJdbcTemplate()
            .query(GET_USER_ID_SQL_BY_MOBILE,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    return new UserId(rs.getString("domain_id"));
                },
                userMobile.getCountryCode(),
                userMobile.getMobileNumber()
            );
        return Optional.ofNullable(query);
    }

    @Override
    public Optional<UserId> queryUserId(UserName username) {
        UserId query = CommonDomainRegistry.getJdbcTemplate()
            .query(GET_USER_ID_SQL_BY_USER_NAME,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    return new UserId(rs.getString("domain_id"));
                },
                username.getValue()
            );
        return Optional.ofNullable(query);
    }

    private static class RowMapper implements ResultSetExtractor<List<User>> {

        @Override
        public List<User> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Collections.emptyList();
            }
            List<User> list = new ArrayList<>();
            long currentId = -1L;
            User user;
            UserPassword userPassword = null;
            if (Utility.notNull(rs.getString("password"))) {
                userPassword = new UserPassword();
                userPassword.setPassword(rs.getString("password"));
            }
            do {
                long dbId = rs.getLong(Auditable.DB_ID);
                if (currentId != dbId) {
                    user = User.fromDatabaseRow(
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_ID),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_CREATED_AT),
                        rs.getString(Auditable.DB_CREATED_BY),
                        DatabaseUtility.getNullableLong(rs, Auditable.DB_MODIFIED_AT),
                        rs.getString(Auditable.DB_MODIFIED_BY),
                        DatabaseUtility.getNullableInteger(rs, Auditable.DB_VERSION),
                        Utility.isNull(rs.getString("email")) ? null :
                            new UserEmail(rs.getString("email")),
                        DatabaseUtility.getNullableBoolean(rs, "locked"),
                        userPassword,
                        new UserId(rs.getString("domain_id")),
                        Utility.isNull(rs.getString("username")) ? null :
                            new UserName(rs.getString("username")),
                        Utility.notNull(rs.getString("country_code")) &&
                            Utility.notNull(rs.getString("mobile_number"))
                            ?
                            new UserMobile(rs.getString("country_code"),
                                rs.getString("mobile_number")) : null,
                        Utility.isNull(rs.getString("avatar_link")) ? null :
                            new UserAvatar(new ImageId(rs.getString("avatar_link"))),
                        Utility.notNull(rs.getString("language")) ?
                            Language.valueOf(rs.getString("language")) : null
                    );
                    list.add(user);
                    currentId = dbId;
                }
            } while (rs.next());
            return list;
        }
    }
}
