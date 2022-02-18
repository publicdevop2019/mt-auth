package com.mt.access.domain.model.project;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface ProjectRepository {
    void add(Project project);

    SumPagedRep<Project> getByQuery(ProjectQuery projectQuery);

    void remove(Project e);

    Optional<Project> getById(ProjectId id);
}
