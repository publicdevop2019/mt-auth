package com.mt.access.domain.model.organization;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
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
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "organizationRegion")
public class Organization extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;

    private String name;

    @Embedded
    private OrganizationId organizationId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))
    })
    private ProjectId projectId;

    public Organization(OrganizationId organizationId, String name) {
        this.id= CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.organizationId = organizationId;
        this.name = name;
    }

    public void replace(String name) {
        this.name = name;
    }
}
