package com.mt.access.domain.model.client;


import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.Utility;
import java.util.Set;

public enum GrantType {
    CLIENT_CREDENTIALS,
    PASSWORD,
    REFRESH_TOKEN,
    AUTHORIZATION_CODE;

    public static void add(Client client, Set<GrantType> grantTypes,
                           Set<RedirectUrl> redirectUrls) {
        validate(grantTypes);
        ClientDomainValidator.grantTypeChange(client, grantTypes, redirectUrls);
        DomainRegistry.getClientGrantTypeRepository().add(client, grantTypes);
    }

    public static void update(Client client, Set<GrantType> oldTypes,
                              Set<GrantType> newTypes, Set<RedirectUrl> redirectUrls,
                              TransactionContext context) {
        if (!Checker.sameAs(oldTypes, newTypes)) {
            validate(newTypes);
            ClientDomainValidator.grantTypeChange(client, newTypes, redirectUrls);
            context.append(new ClientGrantTypeChanged(client.getClientId()));
            Utility.updateSet(oldTypes, newTypes,
                (added) -> DomainRegistry.getClientGrantTypeRepository().add(client, added),
                (removed) -> DomainRegistry.getClientGrantTypeRepository()
                    .remove(client, removed));
        }
    }

    private static void validate(Set<GrantType> grantTypes) {
        Validator.notNull(grantTypes);
        Validator.notEmpty(grantTypes);
        if (grantTypes.contains(GrantType.REFRESH_TOKEN) &&
            !grantTypes.contains(GrantType.PASSWORD)) {
            throw new DefinedRuntimeException("refresh token grant requires password grant", "1037",
                HttpResponseCode.BAD_REQUEST);
        }
    }
}
