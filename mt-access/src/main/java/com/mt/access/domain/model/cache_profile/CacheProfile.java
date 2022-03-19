package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cacheProfileRegion")
@Setter(AccessLevel.PRIVATE)
public class CacheProfile extends Auditable {

    private String name;
    private String description;

    @Embedded
    private CacheProfileId cacheProfileId;

    private boolean allowCache;

    @Convert(converter = CacheControlValue.DBConverter.class)
    private Set<CacheControlValue> cacheControl;

    private Long expires;

    private Long maxAge;

    private Long smaxAge;

    private String vary;

    private boolean etag;

    private boolean weakValidation;

    public CacheProfile(
            String name,
            String description,
            CacheProfileId cacheProfileId,
            Set<CacheControlValue> cacheControl,
            Long expires,
            Long maxAge,
            Long smaxAge,
            String vary,
            boolean allowCache,
            boolean etag,
            boolean weakValidation) {
        super();
        setName(name);
        setDescription(description);
        setAllowCache(allowCache);
        setCacheProfileId(cacheProfileId);
        setCacheControl(cacheControl);
        setExpires(expires);
        setMaxAge(maxAge);
        setSmaxAge(smaxAge);
        setVary(vary);
        setEtag(etag);
        setWeakValidation(weakValidation);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
    }

    public void update(String name,
                       String description,
                       CacheProfileId cacheProfileId,
                       Set<CacheControlValue> cacheControl,
                       Long expires,
                       Long maxAge,
                       Long smaxAge,
                       String vary,
                       boolean allowCache,
                       boolean etag,
                       boolean weakValidation) {
        this.name = name;
        this.description = description;
        this.cacheProfileId = cacheProfileId;
        this.cacheControl = cacheControl;
        this.expires = expires;
        this.maxAge = maxAge;
        this.smaxAge = smaxAge;
        this.vary = vary;
        this.etag = etag;
        this.weakValidation = weakValidation;
        this.allowCache = allowCache;
        DomainEventPublisher.instance().publish(new CacheProfileUpdated(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CacheProfile that = (CacheProfile) o;
        return Objects.equals(cacheProfileId, that.cacheProfileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cacheProfileId);
    }

    public void removeAllReference() {
        DomainEventPublisher.instance().publish(new CacheProfileRemoved(this));
    }
}