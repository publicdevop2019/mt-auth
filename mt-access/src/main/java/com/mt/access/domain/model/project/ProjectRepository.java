package com.mt.access.domain.model.project;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public interface ProjectRepository {
    void add(Project project);

    SumPagedRep<Project> query(ProjectQuery projectQuery);

    void remove(Project e);

    default Project by(ProjectId id){
        Project byIdNullable = byNullable(id);
        Validator.notNull(byIdNullable);
        return byIdNullable;
    }

    Project byNullable(ProjectId id);

    Set<ProjectId> allProjectIds();

    long countTotal();

}
