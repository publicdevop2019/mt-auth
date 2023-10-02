package com.mt.access.domain.model.project;

import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
public class Project extends Auditable {

    private String name;

    private ProjectId projectId;

    public Project(ProjectId projectId, String name, UserId userId, TransactionContext context) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        setName(name);
        this.setCreatedBy(userId.getDomainId());
        long milli = Instant.now().toEpochMilli();
        this.setCreatedAt(milli);
        this.setModifiedAt(milli);
        this.setModifiedBy(userId.getDomainId());
        context.append(new StartNewProjectOnboarding(this));
    }

    public static Project fromDatabaseRow(Long id, Long createAt, String createdBy, Long modifiedAt,
                                          String modifiedBy, Integer version,
                                          String name, ProjectId domainId) {
        Project project = new Project();
        project.setId(id);
        project.setCreatedAt(createAt);
        project.setCreatedBy(createdBy);
        project.setModifiedAt(modifiedAt);
        project.setModifiedBy(modifiedBy);
        project.setVersion(version);
        project.setName(name);
        project.projectId = domainId;
        return project;
    }

    private void setName(String name) {
        Validator.validRequiredString(5, 50, name);
        this.name = name;
    }

}
