package com.mt.access.domain.model.project;

import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    public ProjectId getDefaultProject(Set<ProjectId> projectIdSet) {
        return projectIdSet.stream().findFirst().get();
    }
}
