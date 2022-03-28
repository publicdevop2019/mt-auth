package com.mt.common.domain.model.notification;

import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class PublishedEventTracker {
    @Id
    private final Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    @Version
    private int version;
    @Setter
    @Getter
    private long lastPublishedId;
}
