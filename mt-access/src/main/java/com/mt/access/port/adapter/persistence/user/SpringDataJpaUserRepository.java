package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.MfaInfo;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.access.domain.model.user.UserRepository;
import com.mt.access.domain.model.user.User_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.DatabaseUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    Optional<User> findByEmailEmail(String email);

    default Optional<User> query(UserId userId) {
        return query(new UserQuery(userId)).findFirst();
    }

    default Optional<User> query(UserEmail email) {
        return findByEmailEmail(email.getEmail());
    }

    default Optional<User> queryLoginUser(UserEmail email) {
        Object query = CommonDomainRegistry.getJdbcTemplate()
            .query("SELECT u.domain_id, u.password, u.locked FROM user_ u WHERE u.email = ?",
                new Object[] {email.getEmail()},
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return null;
                        }
                        UserPassword userPassword = new UserPassword();
                        userPassword.setPasswordWithoutEncrypt(rs.getString("password"));
                        return User.loginUser(new UserId(rs.getString("domain_id")), userPassword,
                            rs.getBoolean("locked"));
                    }
                });
        return Optional.ofNullable((User) query);
    }

    default Optional<User> queryLoginUser(UserId userId) {
        Object query = CommonDomainRegistry.getJdbcTemplate()
            .query("SELECT u.domain_id, u.password, u.locked FROM user_ u WHERE u.domain_id = ?",
                new Object[] {userId.getDomainId()},
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return null;
                        }
                        UserPassword userPassword = new UserPassword();
                        userPassword.setPasswordWithoutEncrypt(rs.getString("password"));
                        return User.loginUser(new UserId(rs.getString("domain_id")), userPassword,
                            rs.getBoolean("locked"));
                    }
                });
        return Optional.ofNullable((User) query);
    }

    default Optional<UserId> queryUserId(UserEmail userEmail) {
        Object query = CommonDomainRegistry.getJdbcTemplate()
            .query("SELECT u.domain_id FROM user_ u WHERE u.email = ?",
                new Object[] {userEmail.getEmail()},
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return null;
                        }
                        return new UserId(rs.getString("domain_id"));
                    }
                });
        return Optional.ofNullable((UserId) query);
    }

    default void add(User user) {
        save(user);
    }

    default void remove(User user) {
        delete(user);
    }

    default SumPagedRep<User> query(UserQuery query) {
        return QueryBuilderRegistry.getUserQueryBuilder().execute(query);
    }

    default void batchLock(List<PatchCommand> commands) {
        QueryBuilderRegistry.getUpdateUserQueryBuilder().update(commands, User.class);
    }

    default long countTotal() {
        return countTotal_();
    }

    default Set<UserId> getIds() {
        return getUserIds_();
    }

    @Query("select distinct u.userId from User u")
    Set<UserId> getUserIds_();

    /**
     * update mfa info without trigger version increase & modified at change
     *
     * @param mfaInfo new mfa info
     * @param user    user to udpate
     */
    default void updateMfaInfo(MfaInfo mfaInfo, User user) {
        String sql = "UPDATE user_ u SET u.mfa_id  = ?, u.mfa_code = ? WHERE u.domain_id = ?";
        int update = CommonDomainRegistry.getJdbcTemplate()
            .update(sql, mfaInfo.getId().getValue(), mfaInfo.getCode().getValue(),
                user.getUserId().getDomainId());
        DatabaseUtility.checkUpdate(update);
    }


    @Query("select count(*) from User")
    Long countTotal_();

    @Component
    class JpaCriteriaApiUserAdaptor {

        public SumPagedRep<User> execute(UserQuery userQuery) {
            QueryUtility.QueryContext<User> queryContext =
                QueryUtility.prepareContext(User.class, userQuery);
            Optional.ofNullable(userQuery.getUserEmails()).ifPresent(e -> {
                queryContext.getPredicates().add(UserEmailPredicateConverter
                    .getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                queryContext.getCountPredicates().add(UserEmailPredicateConverter
                    .getPredicate(e, queryContext.getCriteriaBuilder(),
                        queryContext.getCountRoot()));
            });

            Optional.ofNullable(userQuery.getUserIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    User_.USER_ID, queryContext));
            Order order = null;
            if (Checker.isTrue(userQuery.getUserSort().getById())) {
                order = QueryUtility.getDomainIdOrder(User_.USER_ID, queryContext,
                    userQuery.getUserSort().getIsAsc());
            }
            if (Checker.isTrue(userQuery.getUserSort().getByEmail())) {
                order = QueryUtility
                    .getOrder(User_.EMAIL, queryContext, userQuery.getUserSort().getIsAsc());
            }
            if (Checker.isTrue(userQuery.getUserSort().getByCreateAt())) {
                order = QueryUtility
                    .getOrder(User_.CREATED_AT, queryContext, userQuery.getUserSort().getIsAsc());
            }
            if (Checker.isTrue(userQuery.getUserSort().getByLocked())) {
                order = QueryUtility
                    .getOrder(User_.LOCKED, queryContext, userQuery.getUserSort().getIsAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(userQuery, queryContext);
        }

        public static class UserEmailPredicateConverter {
            public static Predicate getPredicate(Set<String> values, CriteriaBuilder cb,
                                                 Root<User> root) {
                List<Predicate> results = new ArrayList<>();
                for (String str : values) {
                    results.add(cb.like(root.get("email").get("email"), "%" + str + "%"));
                }
                return cb.or(results.toArray(new Predicate[0]));
            }
        }
    }
}
