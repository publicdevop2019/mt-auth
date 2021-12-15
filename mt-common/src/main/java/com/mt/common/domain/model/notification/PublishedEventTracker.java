package com.mt.common.domain.model.notification;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

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
    @Getter
    @Setter
    private boolean skipped;
}
