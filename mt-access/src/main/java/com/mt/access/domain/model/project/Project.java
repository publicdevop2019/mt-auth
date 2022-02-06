package com.mt.access.domain.model.project;

import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "projectRegion")
public class Project extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;

    private String name;

    @Embedded
    private ProjectId projectId;

    public Project(ProjectId projectId, String name, UserId userId) {
        this.id=CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        this.name = name;
        this.setCreatedBy(userId.getDomainId());
        DomainEventPublisher.instance().publish(new ProjectCreated(this));
    }

    public void replace(String name) {
        this.name = name;
    }
}
