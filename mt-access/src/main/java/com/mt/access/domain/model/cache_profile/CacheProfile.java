package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * cache configuration for http.
 */
@Slf4j
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class CacheProfile extends Auditable {

    private String name;
    private String description;

    private CacheProfileId cacheProfileId;

    private Boolean allowCache;

    @Getter
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

    @Setter(AccessLevel.PRIVATE)
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
        long epochMilli = Instant.now().toEpochMilli();
        setCreatedAt(epochMilli);
        setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        setModifiedAt(epochMilli);
        setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
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

    public static CacheProfile fromDatabaseRow(Long id, Long createAt, String createBy,
                                               Long modifiedAt,
                                               String modifiedBy, Integer version,
                                               Boolean allowCache,
                                               CacheProfileId domainId, String description,
                                               Boolean etag,
                                               Long expires, Long maxAge, String name, Long smaxAge,
                                               String vary, Boolean weakValidation,
                                               ProjectId projectId) {
        CacheProfile cacheProfile = new CacheProfile();
        cacheProfile.setId(id);
        cacheProfile.setCreatedAt(createAt);
        cacheProfile.setCreatedBy(createBy);
        cacheProfile.setModifiedAt(modifiedAt);
        cacheProfile.setModifiedBy(modifiedBy);
        cacheProfile.setVersion(version);
        cacheProfile.setAllowCache(allowCache);
        cacheProfile.setCacheProfileId(domainId);
        cacheProfile.setDescription(description);
        cacheProfile.setEtag(etag);
        cacheProfile.setExpires(expires);
        cacheProfile.setMaxAge(maxAge);
        cacheProfile.setName(name);
        cacheProfile.setSmaxAge(smaxAge);
        cacheProfile.setVary(vary);
        cacheProfile.setWeakValidation(weakValidation);
        cacheProfile.setProjectId(projectId);
        return cacheProfile;
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
        if (Utility.notNull(smaxAge)) {
            Validator.lessThanOrEqualTo(smaxAge, 31536000);
            Validator.greaterThanOrEqualTo(smaxAge, 1);
        }
        this.smaxAge = smaxAge;
    }

    private void setMaxAge(Long maxAge) {
        if (Utility.notNull(maxAge)) {
            Validator.lessThanOrEqualTo(maxAge, 31536000);
            Validator.greaterThanOrEqualTo(maxAge, 1);
        }
        this.maxAge = maxAge;
    }

    private void setExpires(Long expires) {
        if (Utility.notNull(expires)) {
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
        if (Utility.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name.trim();
    }

    /**
     * immutable update, return new updated CacheProfile.
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
     * @return updated {@code CacheProfile}
     */
    public CacheProfile update(
        String name,
        String description,
        Set<CacheControlValue> cacheControl,
        Long expires,
        Long maxAge,
        Long smaxAge,
        String vary,
        Boolean allowCache,
        Boolean etag,
        Boolean weakValidation,
        TransactionContext context
    ) {
        CacheProfile updated =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, CacheProfile.class);

        updated.setName(name);
        updated.setDescription(description);
        updated.setCacheControl(cacheControl);
        updated.setExpires(expires);
        updated.setMaxAge(maxAge);
        updated.setSmaxAge(smaxAge);
        updated.setVary(vary);
        updated.setEtag(etag);
        updated.setWeakValidation(weakValidation);
        updated.setAllowCache(allowCache);

        if (this.keyFieldsChanged(updated)) {
            log.debug("domain object updated");
            context.append(new CacheProfileUpdated(this));
        }

        updated.validate(new HttpValidationNotificationHandler());
        DomainRegistry.getCacheProfileValidationService()
            .validate(updated, new HttpValidationNotificationHandler());
        return updated;
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

    public void removeAllReference(TransactionContext context) {
        context.append(new CacheProfileRemoved(this));
    }

    private boolean keyFieldsChanged(CacheProfile o) {
        return
            !Objects.equals(allowCache, o.allowCache) ||
                !Objects.equals(cacheControl, o.cacheControl) ||
                !Objects.equals(expires, o.expires) ||
                !Objects.equals(maxAge, o.maxAge) ||
                !Objects.equals(smaxAge, o.smaxAge) ||
                !Objects.equals(vary, o.vary) && Objects.equals(etag, o.etag) ||
                !Objects.equals(weakValidation, o.weakValidation);
    }
}