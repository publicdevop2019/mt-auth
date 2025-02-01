package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.Utility;
import java.util.Set;

public class ExposedHeader {
    public static void add(CorsProfile corsProfile, Set<String> headers) {
        Validator.validOptionalCollection(10, headers);
        if (Checker.notNull(headers)) {
            HeaderNameValidator.validateHeaderName(headers);
        }
        DomainRegistry.getCorsExposedHeaderRepository().add(corsProfile, headers);
    }

    public static void update(CorsProfile updated, Set<String> old, Set<String> next,
                              TransactionContext context) {
        if (!Checker.sameAs(old, next)) {
            context.append(new CorsProfileUpdated(updated));
            Validator.validOptionalCollection(10, next);
            if (Checker.notNull(next)) {
                HeaderNameValidator.validateHeaderName(next);
            }
            Utility.updateSet(old, next,
                (added) -> DomainRegistry.getCorsExposedHeaderRepository().add(updated, added),
                (removed) -> DomainRegistry.getCorsExposedHeaderRepository()
                    .remove(updated, removed));
        }
    }
}
