package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class RedirectDetail implements Serializable {

    private Set<RedirectUrl> redirectUrls;

    @Getter
    private boolean urlLoaded = false;

    public RedirectDetail(Set<String> redirectUrls) {
        setRedirectUrls(redirectUrls);
    }

    private void setRedirectUrls(Set<String> redirectUrls) {
        Validator.notNull(redirectUrls);
        Validator.notEmpty(redirectUrls);
        Validator.lessThanOrEqualTo(redirectUrls, 5);
        Set<RedirectUrl> collect =
            redirectUrls.stream().map(RedirectUrl::new).collect(Collectors.toSet());
        CommonUtility.updateCollection(this.redirectUrls, collect,
            () -> this.redirectUrls = collect);
        //set to true so url will not be loaded again
        urlLoaded = true;
    }

    public Set<RedirectUrl> getRedirectUrls(Client client) {
        if (client.isCreate()) {
            return redirectUrls;
        }
        if (!urlLoaded) {
            redirectUrls =
                DomainRegistry.getClientRepository().getRedirectUrls(client.getId());
            urlLoaded = true;
        }
        return redirectUrls;
    }
}
