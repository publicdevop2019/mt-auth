package com.mt.access.domain.model.position;

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
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "positionRegion")
public class Position extends Auditable {

    private String name;

    @Embedded
    private PositionId positionId;

    public Position(PositionId positionId, String name) {
        super();
        this.id= CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.positionId = positionId;
        this.name = name;
    }

    public void replace(String name) {
        this.name = name;
    }
}
