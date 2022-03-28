package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

/**
 * cache profile for http.
 */
@Entity
@Table
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "cacheProfileRegion")
@Setter(AccessLevel.PRIVATE)
public class CacheProfile extends Auditable {

    private String name;
    private String description;

    @Embedded
    private CacheProfileId cacheProfileId;

    private boolean allowCache;

    @Convert(converter = CacheControlValue.DbConverter.class)
    private Set<CacheControlValue> cacheControl;

    private Long expires;

    private Long maxAge;

    private Long smaxAge;

    private String vary;

    private boolean etag;

    private boolean weakValidation;

    /**
     * constructor of CacheProfile.
     *
     * @param name           profile name
     * @param description    profile description
     * @param cacheProfileId domain id
     * @param cacheControl   value of cache control
     * @param expires        value of expires
     * @param maxAge         value of max age
     * @param smaxAge        value of server max age
     * @param vary           value of vary
     * @param allowCache     whether cache is allowed
     * @param etag           value of etag
     * @param weakValidation whether weak validation
     */
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

    /**
     * update CacheProfile.
     *
     * @param name           profile name
     * @param description    profile description
     * @param cacheControl   value of cache control
     * @param expires        value of expires
     * @param maxAge         value of max age
     * @param smaxAge        value of server max age
     * @param vary           value of vary
     * @param allowCache     whether cache is allowed
     * @param etag           value of etag
     * @param weakValidation whether weak validation
     */
    public void update(String name,
                       String description,
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
        this.cacheControl = cacheControl;
        this.expires = expires;
        this.maxAge = maxAge;
        this.smaxAge = smaxAge;
        this.vary = vary;
        this.etag = etag;
        this.weakValidation = weakValidation;
        this.allowCache = allowCache;
        CommonDomainRegistry.getDomainEventRepository().append(new CacheProfileUpdated(this));
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
        CacheProfile that = (CacheProfile) o;
        return Objects.equals(cacheProfileId, that.cacheProfileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cacheProfileId);
    }

    public void removeAllReference() {
        CommonDomainRegistry.getDomainEventRepository().append(new CacheProfileRemoved(this));
    }
}