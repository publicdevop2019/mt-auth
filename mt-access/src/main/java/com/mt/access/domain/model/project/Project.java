package com.mt.access.domain.model.project;

import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Cacheable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
@EqualsAndHashCode(callSuper = true)
@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "projectRegion")
public class Project extends Auditable {

    private String name;

    @Embedded
    private ProjectId projectId;

    public Project(ProjectId projectId, String name, UserId userId) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        setName(name);
        this.setCreatedBy(userId.getDomainId());
        CommonDomainRegistry.getDomainEventRepository().append(new StartNewProjectOnboarding(this));
    }

    private void setName(String name) {
        Validator.validRequiredString(5, 50, name);
        this.name = name;
    }

    public void replace(String name) {
        this.name = name;
    }
}
