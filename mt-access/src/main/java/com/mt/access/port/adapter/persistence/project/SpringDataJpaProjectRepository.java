package com.mt.access.port.adapter.persistence.project;

import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.project.ProjectRepository;
import com.mt.access.domain.model.project.Project_;
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

public interface SpringDataJpaProjectRepository
    extends ProjectRepository, JpaRepository<Project, Long> {
    default Optional<Project> getById(ProjectId id) {
        return getByQuery(new ProjectQuery(id)).findFirst();
    }

    default void add(Project project) {
        save(project);
    }

    default void remove(Project project) {
        delete(project);
    }

    default SumPagedRep<Project> getByQuery(ProjectQuery query) {
        return QueryBuilderRegistry.getProjectAdaptor().execute(query);
    }

    default Set<ProjectId> allProjectIds() {
        return getProjectIds_();
    }

    default long countTotal() {
        return countTotal_();
    }

    @Query("select distinct ep.projectId from Project ep")
    Set<ProjectId> getProjectIds_();

    @Query("select count(*) from Project")
    Long countTotal_();

    @Component
    class JpaCriteriaApiProjectAdaptor {
        public SumPagedRep<Project> execute(ProjectQuery query) {
            QueryUtility.QueryContext<Project> queryContext =
                QueryUtility.prepareContext(Project.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Project_.PROJECT_ID, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility
                    .getDomainIdOrder(Project_.PROJECT_ID, queryContext, query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }
}
