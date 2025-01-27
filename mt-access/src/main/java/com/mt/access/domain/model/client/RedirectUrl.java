package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode
public class RedirectUrl implements Serializable {
    private static final long serialVersionUID = 1;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public RedirectUrl(String url) {
        Validator.notNull(url);
        Validator.isHttpUrl(url);
        value = url;
    }

    public static void add(Client client, Set<GrantType> grantTypes,
                           Set<RedirectUrl> redirectUrls) {
        ClientDomainValidator.redirectUrlChange(grantTypes, redirectUrls);
        if (Utility.notNullOrEmpty(redirectUrls)) {
            Validator.lessThanOrEqualTo(redirectUrls, 5);
            DomainRegistry.getClientRedirectUrlRepository().add(client, redirectUrls);
        }
    }

    public static void update(Client client, Set<GrantType> grantTypes, Set<RedirectUrl> oldUrls,
                              Set<RedirectUrl> newUrls) {
        if (!Utility.sameAs(oldUrls, newUrls)) {
            Validator.lessThanOrEqualTo(newUrls, 5);
            ClientDomainValidator.redirectUrlChange(grantTypes, newUrls);
            Utility.updateSet(oldUrls, newUrls,
                (added) -> DomainRegistry.getClientRedirectUrlRepository().add(client, added),
                (removed) -> DomainRegistry.getClientRedirectUrlRepository()
                    .remove(client, removed));
        }
    }

}
