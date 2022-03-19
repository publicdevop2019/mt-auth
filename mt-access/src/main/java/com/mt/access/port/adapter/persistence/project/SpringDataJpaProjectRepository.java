package com.mt.access.port.adapter.persistence.project;

import com.mt.access.domain.model.project.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SpringDataJpaProjectRepository extends ProjectRepository, JpaRepository<Project, Long> {
    default Optional<Project> getById(ProjectId id) {
        return getByQuery(new ProjectQuery(id)).findFirst();
    }

    default void add(Project project) {
        save(project);
    }

    default void remove(Project project) {
        project.softDelete();
        save(project);
    }

    default SumPagedRep<Project> getByQuery(ProjectQuery query) {
        return QueryBuilderRegistry.getProjectAdaptor().execute(query);
    }

    @Component
    class JpaCriteriaApiProjectAdaptor {
        public SumPagedRep<Project> execute(ProjectQuery query) {
            QueryUtility.QueryContext<Project> queryContext = QueryUtility.prepareContext(Project.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                    .addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Project_.PROJECT_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Project_.PROJECT_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
