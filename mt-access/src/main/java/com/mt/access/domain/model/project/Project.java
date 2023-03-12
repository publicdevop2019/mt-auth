package com.mt.access.domain.model.project;

import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.audit.NextAuditable;
import com.mt.common.domain.model.validate.Validator;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "projectRegion")
public class Project extends NextAuditable {

    private String name;

    @Embedded
    private ProjectId projectId;

    public Project(ProjectId projectId, String name, UserId userId) {
        super();
        Validator.notBlank(name);
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        this.name = name;
        this.setCreatedBy(userId.getDomainId());
        CommonDomainRegistry.getDomainEventRepository().append(new StartNewProjectOnboarding(this));
    }

    public void replace(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Project project = (Project) o;
        return Objects.equals(projectId, project.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), projectId);
    }
}
