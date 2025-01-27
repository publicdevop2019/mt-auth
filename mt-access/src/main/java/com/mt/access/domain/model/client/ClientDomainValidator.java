package com.mt.access.domain.model.client;

import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Set;

public class ClientDomainValidator {
    public static void grantTypeChange(Client client, Set<GrantType> grantTypes,
                                       Set<RedirectUrl> redirectUrls) {
        grantTypeAndRedirectUrls(grantTypes, redirectUrls);
        onlyMainCanHavePasswordGrant(client, grantTypes);
        tokenAndGrantType(client, grantTypes);
    }

    public static void redirectUrlChange(Set<GrantType> grantTypes,
                                         Set<RedirectUrl> redirectUrls) {
        grantTypeAndRedirectUrls(grantTypes, redirectUrls);
    }

    private static void grantTypeAndRedirectUrls(Set<GrantType> grantTypes,
                                                 Set<RedirectUrl> redirectUrls) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        if (
            grantTypes.contains(GrantType.AUTHORIZATION_CODE)
                && redirectUrls.isEmpty()
        ) {
            handler
                .handleError("redirect details and authorization grant must both exist");
        }
        if (
            !grantTypes.contains(GrantType.AUTHORIZATION_CODE)
                && !redirectUrls.isEmpty()
        ) {
            handler
                .handleError("redirect details and authorization grant must both exist");
        }
    }

    private static void onlyMainCanHavePasswordGrant(Client client, Set<GrantType> grantTypes) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        if (Utility.notNullOrEmpty(grantTypes)) {
            if (grantTypes.contains(GrantType.PASSWORD)) {
                if (!AppConstant.MAIN_PROJECT_ID.equalsIgnoreCase(
                    client.getProjectId().getDomainId())) {
                    handler
                        .handleError("only main project can have password grant");
                }
            }
        }
    }

    private static void tokenAndGrantType(Client client, Set<GrantType> grantTypes) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        if (grantTypes != null && !grantTypes.isEmpty()) {
            if (client.getTokenDetail().getAccessTokenValiditySeconds() == null
                ||
                client.getTokenDetail().getAccessTokenValiditySeconds() < 60) {
                handler
                    .handleError("when grant present access token validity seconds must be valid");
            }
            if (grantTypes.contains(GrantType.REFRESH_TOKEN)) {
                if (client.getTokenDetail().getRefreshTokenValiditySeconds() == null
                    ||
                    client.getTokenDetail().getRefreshTokenValiditySeconds() < 120) {
                    handler
                        .handleError("refresh grant must has valid refresh token validity seconds");
                }
            } else {
                if (client.getTokenDetail().getRefreshTokenValiditySeconds() != null) {
                    handler
                        .handleError("refresh token validity seconds requires refresh grant");
                }
            }
        }
    }
}
