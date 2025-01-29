package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

@NoArgsConstructor
@EqualsAndHashCode
public class Origin implements Serializable {
    private static final UrlValidator defaultValidator =
        new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    private static final String ALL = "*";
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public Origin(String url) {
        if (defaultValidator.isValid(url) || ALL.equalsIgnoreCase(url)) {
            value = url;
        } else {
            throw new DefinedRuntimeException("invalid origin value", "1039",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void add(CorsProfile corsProfile, Set<Origin> origins) {
        Validator.validRequiredCollection(1, 5, origins);
        DomainRegistry.getCorsOriginRepository().add(corsProfile, origins);
    }

    public static void update(CorsProfile updated, Set<Origin> old, Set<Origin> next,
                              TransactionContext context) {
        if (!Utility.sameAs(old, next)) {
            context.append(new CorsProfileUpdated(updated));
            Validator.validRequiredCollection(1, 5, next);
            Utility.updateSet(old, next,
                (added) -> DomainRegistry.getCorsOriginRepository().add(updated, added),
                (removed) -> DomainRegistry.getCorsOriginRepository()
                    .remove(updated, removed));
        }
    }
}
