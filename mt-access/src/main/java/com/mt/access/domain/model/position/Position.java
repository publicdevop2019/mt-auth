package com.mt.access.domain.model.position;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.NextAuditable;
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
    region = "positionRegion")
public class Position extends NextAuditable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return Objects.equals(positionId, position.positionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionId);
    }
}
