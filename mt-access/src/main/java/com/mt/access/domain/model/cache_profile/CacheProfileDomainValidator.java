package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Set;

public class CacheProfileDomainValidator {
    public static void validate(CacheProfile cacheProfile, Set<CacheControlValue> values) {
        validateCacheableFlag(cacheProfile, values);
    }

    private static void validateCacheableFlag(CacheProfile cacheProfile,
                                              Set<CacheControlValue> cacheControlValues) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        if (Utility.isFalse(cacheProfile.getAllowCache())) {
            if (Utility.notNull(cacheControlValues)) {
                Validator.isEmpty(cacheControlValues);
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
                (Utility.isNull(cacheControlValues) ||
                    Utility.isEmpty(cacheControlValues)) &&
                    Utility.isNull(cacheProfile.getVary()) &&
                    Utility.isNull(cacheProfile.getMaxAge()) &&
                    Utility.isNull(cacheProfile.getSmaxAge()) &&
                    Utility.isNull(cacheProfile.getExpires()) &&
                    Utility.isNull(cacheProfile.getEtag())
            ) {
                handler.handleError("should have cache config");
            }
        }
    }
}
