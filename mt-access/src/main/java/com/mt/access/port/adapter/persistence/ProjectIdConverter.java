package com.mt.access.port.adapter.persistence;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.domain_event.DomainId;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;

public class ProjectIdConverter implements AttributeConverter<ProjectId, String> {
    @Override
    public String convertToDatabaseColumn(ProjectId projectId) {
        if (projectId == null) {
            return null;
        }
        return projectId.getDomainId();
    }

    @Override
    public ProjectId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData.isBlank() || dbData.isEmpty()) {
            return null;
        }
        return new ProjectId(dbData);
    }
}
