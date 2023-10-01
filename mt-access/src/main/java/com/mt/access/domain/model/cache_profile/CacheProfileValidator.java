package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.validate.Checker;
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
        if (Checker.notNull(cacheProfile.getMaxAge())) {
            Validator.isNull(cacheProfile.getSmaxAge());
        }
        if (Checker.notNull(cacheProfile.getSmaxAge())) {
            Validator.isNull(cacheProfile.getMaxAge());
        }
    }

    private void validateETag() {
        if (Checker.notNull(cacheProfile.getWeakValidation())) {
            Validator.isTrue(cacheProfile.getEtag());
        }
    }

    private void validateCacheableFlag() {
        if (Checker.isFalse(cacheProfile.getAllowCache())) {
            if (Checker.notNull(cacheProfile.getCacheControl())) {
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
                (Checker.isNull(cacheProfile.getCacheControl()) ||
                    Checker.isEmpty(cacheProfile.getCacheControl())) &&
                    Checker.isNull(cacheProfile.getVary()) &&
                    Checker.isNull(cacheProfile.getMaxAge()) &&
                    Checker.isNull(cacheProfile.getSmaxAge()) &&
                    Checker.isNull(cacheProfile.getExpires()) &&
                    Checker.isNull(cacheProfile.getEtag())
            ) {
                handler
                    .handleError("should have cache config");
            }
        }
    }
}
