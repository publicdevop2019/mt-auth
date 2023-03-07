package com.mt.access.port.adapter.persistence.user;

import static com.mt.access.infrastructure.AppConstant.MT_AUTH_PROJECT_ID;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationQuery;
import com.mt.access.domain.model.user.UserRelationRepository;
import com.mt.access.domain.model.user.UserRelation_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

public interface SpringDataJpaUserRelationRepository
    extends UserRelationRepository, JpaRepository<UserRelation, Long> {

    default SumPagedRep<UserRelation> getByUserId(UserId id) {
        return getByQuery(new UserRelationQuery(id));
    }

    default void add(UserRelation role) {
        save(role);
    }

    default void remove(UserRelation userRelation) {
        userRelation.softDelete();
        save(userRelation);
    }

    default Set<ProjectId> getProjectIds() {
        return getProjectIds_();
    }

    default SumPagedRep<UserRelation> getByQuery(UserRelationQuery query) {
        if (query.getEmailLike() != null) {
            return searchUserByEmailLike(query);
        }
        return QueryBuilderRegistry.getUserRelationAdaptor().execute(query);
    }

    default long countProjectOwnedTotal(ProjectId projectId) {
        return countProjectOwnedTotal_(projectId);
    }

    default long countProjectAdmin(RoleId roleId) {
        return countProjectAdmin_(roleId, new ProjectId(MT_AUTH_PROJECT_ID));
    }

    @Query("select distinct c.projectId from UserRelation c")
    Set<ProjectId> getProjectIds_();


    @Query("select count(*) from UserRelation u where u.projectId = ?1")
    long countProjectOwnedTotal_(ProjectId projectId);

    default SumPagedRep<UserRelation> searchUserByEmailLike(UserRelationQuery query) {
        EntityManager entityManager = QueryUtility.getEntityManager();
        TypedQuery<UserRelation> dataQuery =
            entityManager.createNamedQuery("findEmailLike", UserRelation.class);
        dataQuery.setHint("org.hibernate.cacheable", true);
        dataQuery.setParameter("emailLike", "%" + query.getEmailLike() + "%");
        dataQuery.setParameter("projectId", query.getProjectIds().toArray()[0]);
        List<UserRelation> data = dataQuery
            .setFirstResult(BigDecimal.valueOf(query.getPageConfig().getOffset()).intValue())
            .setMaxResults(query.getPageConfig().getPageSize())
            .getResultList();
        TypedQuery<Long> countQuery =
            entityManager.createNamedQuery("findEmailLikeCount", Long.class);
        countQuery.setHint("org.hibernate.cacheable", true);
        countQuery.setParameter("emailLike", "%" + query.getEmailLike() + "%");
        countQuery.setParameter("projectId", query.getProjectIds().toArray()[0]);
        Long count = countQuery.getSingleResult();
        return new SumPagedRep<>(data, count);
    }

    default Long countProjectAdmin_(RoleId roleId, ProjectId projectId) {
        EntityManager entityManager = QueryUtility.getEntityManager();
        javax.persistence.Query countQuery = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM user_relation ur WHERE ur.standalone_roles LIKE :adminRole AND ur.project_id = :projectId");
//      countQuery.setHint("org.hibernate.cacheable", true);//@note will cause error
//      ref: https://stackoverflow.com/questions/25789176/aliases-expected-length-is-0-actual-length-is-1-on-hibernate-query-cache
        countQuery.setParameter("adminRole", "%" + roleId.getDomainId() + "%");
        countQuery.setParameter("projectId", projectId.getDomainId());
        return ((Number) countQuery.getSingleResult()).longValue();
    }

    @Component
    class JpaCriteriaApiUserRelationAdaptor {
        public SumPagedRep<UserRelation> execute(UserRelationQuery query) {
            QueryUtility.QueryContext<UserRelation> queryContext =
                QueryUtility.prepareContext(UserRelation.class, query);
            Optional.ofNullable(query.getUserIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    UserRelation_.USER_ID, queryContext));
            Optional.ofNullable(query.getProjectIds())
                .ifPresent(
                    e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId)
                        .collect(Collectors.toSet()), UserRelation_.PROJECT_ID, queryContext));
            Optional.ofNullable(query.getRoleId())
                .ifPresent(
                    e -> QueryUtility.addStringLikePredicate(e.getDomainId(),
                        UserRelation_.STANDALONE_ROLES, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility
                    .getDomainIdOrder(UserRelation_.USER_ID, queryContext, query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
