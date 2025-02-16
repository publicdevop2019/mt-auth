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
}
