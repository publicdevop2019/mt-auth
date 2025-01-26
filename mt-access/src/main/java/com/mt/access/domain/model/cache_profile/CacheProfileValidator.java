package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;

public class CacheProfileValidator {
    private final CacheProfile cacheProfile;
    private final ValidationNotificationHandler handler;

    public CacheProfileValidator(CacheProfile cacheProfile, ValidationNotificationHandler handler) {
        this.cacheProfile = cacheProfile;
        this.handler = handler;
    }

    public void validate() {
        validateCacheableFlag();
        validateETag();
        validateMaxAgeAndSMaxAge();
    }

    private void validateMaxAgeAndSMaxAge() {
        if (Utility.notNull(cacheProfile.getMaxAge())) {
            Validator.isNull(cacheProfile.getSmaxAge());
        }
        if (Utility.notNull(cacheProfile.getSmaxAge())) {
            Validator.isNull(cacheProfile.getMaxAge());
        }
    }

    private void validateETag() {
        if (Utility.notNull(cacheProfile.getWeakValidation())) {
            Validator.isTrue(cacheProfile.getEtag());
        }
    }

    private void validateCacheableFlag() {
        if (Utility.isFalse(cacheProfile.getAllowCache())) {
            if (Utility.notNull(cacheProfile.getCacheControl())) {
                Validator.isEmpty(cacheProfile.getCacheControl());
            }
            Validator.isNull(cacheProfile.getExpires());
            Validator.isNull(cacheProfile.getMaxAge());
            Validator.isNull(cacheProfile.getSmaxAge());
            Validator.isNull(cacheProfile.getVary());
            Validator.isNull(cacheProfile.getWeakValidation());
            Validator.isNull(cacheProfile.getEtag());
        } else {
            //at lease some cache configuration should present
            if (
                (Utility.isNull(cacheProfile.getCacheControl()) ||
                    Utility.isEmpty(cacheProfile.getCacheControl())) &&
                    Utility.isNull(cacheProfile.getVary()) &&
                    Utility.isNull(cacheProfile.getMaxAge()) &&
                    Utility.isNull(cacheProfile.getSmaxAge()) &&
                    Utility.isNull(cacheProfile.getExpires()) &&
                    Utility.isNull(cacheProfile.getEtag())
            ) {
                handler
                    .handleError("should have cache config");
            }
        }
    }
}
