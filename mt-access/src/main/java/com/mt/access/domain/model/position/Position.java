package com.mt.access.domain.model.position;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import javax.persistence.Cacheable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "positionRegion")
@EqualsAndHashCode(callSuper = true)
public class Position extends Auditable {

    private String name;

    @Embedded
    private PositionId positionId;

    public Position(PositionId positionId, String name) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.positionId = positionId;
        this.name = name;
    }

    public void replace(String name) {
        this.name = name;
    }

}
