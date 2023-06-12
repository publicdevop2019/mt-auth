package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import org.springframework.stereotype.Service;

@Service
public class CacheProfileValidationService {
    public void validate(CacheProfile cacheProfile, ValidationNotificationHandler handler) {
        hasValidProjectId(cacheProfile);
    }

    private void hasValidProjectId(CacheProfile cacheProfile) {
        ProjectId projectId = cacheProfile.getProjectId();
        Project query = DomainRegistry.getProjectRepository().query(projectId);
        Validator.notNull(query);
    }
}
