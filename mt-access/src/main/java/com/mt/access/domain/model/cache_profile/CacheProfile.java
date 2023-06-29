package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * cache configuration for http.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table
@Slf4j
@NoArgsConstructor
@Getter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "cacheProfileRegion")
@ToString
public class CacheProfile extends Auditable {

    private String name;
    private String description;

    @Embedded
    private CacheProfileId cacheProfileId;

    private Boolean allowCache;

    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "cache_control_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "cache_control")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "cacheControlRegion")
    @Enumerated(EnumType.STRING)
    private Set<CacheControlValue> cacheControl = new LinkedHashSet<>();
    /**
     * HTTP header contains the date/time after which the response is considered expired.
     * ignored if maxAge or smaxAge
     */
    private Long expires;
    /**
     * indicates that the response remains fresh until N seconds after the response is generated.
     * min 1 sec
     * max 31536000 = 1 year
     */
    private Long maxAge;
    /**
     * indicates how long the response is fresh for (similar to max-age)
     * but it is specific to shared caches, and will ignore max-age when it is present.
     */
    private Long smaxAge;
    /**
     * used to create a cache key when content negotiation is in use
     * aside from the method and URL
     * "*" implies that the response is uncacheable
     */
    private String vary;

    private Boolean etag;

    private Boolean weakValidation;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "projectId", updatable = false, nullable = false))
    })
    private ProjectId projectId;

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
     * @param projectId      tenant project id
     */
    public CacheProfile(
        String name,
        String description,
        CacheProfileId cacheProfileId,
        Set<String> cacheControl,
        Long expires,
        Long maxAge,
        Long smaxAge,
        String vary,
        Boolean allowCache,
        Boolean etag,
        Boolean weakValidation,
        ProjectId projectId
    ) {
        super();
        setName(name);
        setDescription(description);
        setAllowCache(allowCache);
        setCacheProfileId(cacheProfileId);
        setCacheControl(CommonUtility.map(cacheControl, CacheControlValue::valueOfLabel));
        setExpires(expires);
        setMaxAge(maxAge);
        setSmaxAge(smaxAge);
        setVary(vary);
        setEtag(etag);
        setWeakValidation(weakValidation);
        setProjectId(projectId);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getCacheProfileValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setWeakValidation(Boolean weakValidation) {
        this.weakValidation = weakValidation;
    }

    private void setEtag(Boolean etag) {
        this.etag = etag;
    }

    private void setVary(String vary) {
        Validator.validOptionalString(100, vary);
        this.vary = vary;
    }

    private void setSmaxAge(Long smaxAge) {
        if (Checker.notNull(smaxAge)) {
            Validator.lessThanOrEqualTo(smaxAge, 31536000);
            Validator.greaterThanOrEqualTo(smaxAge, 1);
        }
        this.smaxAge = smaxAge;
    }

    private void setMaxAge(Long maxAge) {
        if (Checker.notNull(maxAge)) {
            Validator.lessThanOrEqualTo(maxAge, 31536000);
            Validator.greaterThanOrEqualTo(maxAge, 1);
        }
        this.maxAge = maxAge;
    }

    private void setExpires(Long expires) {
        if (Checker.notNull(expires)) {
            Validator.lessThanOrEqualTo(expires, 31536000);
            Validator.greaterThanOrEqualTo(expires, 1);
        }
        this.expires = expires;
    }

    private void setCacheProfileId(CacheProfileId cacheProfileId) {
        Validator.notNull(cacheProfileId);
        this.cacheProfileId = cacheProfileId;
    }

    private void setAllowCache(Boolean allowCache) {
        Validator.notNull(allowCache);
        this.allowCache = allowCache;
    }

    private void setDescription(String description) {
        Validator.validOptionalString(100, description);
        if (Checker.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name.trim();
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
                       Boolean allowCache,
                       Boolean etag,
                       Boolean weakValidation) {
        CacheProfile original =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, CacheProfile.class);
        //exclude name and description from comparison and bypass setter check
        setName(name);
        setDescription(description);
        setCacheControl(cacheControl);
        setExpires(expires);
        setMaxAge(maxAge);
        setSmaxAge(smaxAge);
        setVary(vary);
        setEtag(etag);
        setWeakValidation(weakValidation);
        setAllowCache(allowCache);
        CacheProfile afterUpdate =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, CacheProfile.class);
        original.name = null;
        original.description = null;
        afterUpdate.name = null;
        afterUpdate.description = null;
        log.debug("original hashcode is {}, updated hashcode is{}", original.hashCode(),
            afterUpdate.hashCode());
        if (!afterUpdate.equals(original)) {
            log.debug("domain object updated");
            CommonDomainRegistry.getDomainEventRepository().append(new CacheProfileUpdated(this));
        }
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getCacheProfileValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setCacheControl(Set<CacheControlValue> cacheControl) {
        Validator.validOptionalCollection(9, cacheControl);
        CommonUtility.updateCollection(this.cacheControl, cacheControl,
            () -> this.cacheControl = cacheControl);
    }

    @Override
    public void validate(ValidationNotificationHandler handler) {
        (new CacheProfileValidator(this, handler)).validate();
    }

    public void removeAllReference() {
        CommonDomainRegistry.getDomainEventRepository().append(new CacheProfileRemoved(this));
    }

    public void updateNameAndDescription(String name, String description) {
        setName(name);
        setDescription(description);
    }
}