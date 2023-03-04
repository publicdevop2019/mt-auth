package com.mt.access.port.adapter.persistence.user_relation;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.access.domain.model.user_relation.UserRelationQuery;
import com.mt.access.domain.model.user_relation.UserRelationRepository;
import com.mt.access.domain.model.user_relation.UserRelation_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
        return QueryBuilderRegistry.getUserRelationAdaptor().execute(query);
    }
    default long countProjectOwnedTotal(ProjectId projectId){
        return countProjectOwnedTotal_(projectId);
    }
    @Query("select distinct c.projectId from UserRelation c")
    Set<ProjectId> getProjectIds_();
    @Query("select count(*) from UserRelation u where u.projectId = ?1")
    long countProjectOwnedTotal_(ProjectId projectId);

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
