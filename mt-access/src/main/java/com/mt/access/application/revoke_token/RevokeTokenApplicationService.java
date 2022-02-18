package com.mt.access.application.revoke_token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.event.*;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;
import com.mt.access.domain.model.revoke_token.RevokeTokenQuery;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserDeleted;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RevokeTokenApplicationService {

    public static final String REVOKE_TOKEN = "RevokeToken";

    @SubscribeForEvent
    @Transactional
    public String create(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            DomainRegistry.getRevokeTokenRepository().add(new RevokeToken(revokeTokenId));
            return revokeTokenId.getDomainId();
        }, REVOKE_TOKEN);
    }

    @SubscribeForEvent
    @Transactional
    public String internalOnlyCreate(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            DomainRegistry.getRevokeTokenRepository().add(new RevokeToken(revokeTokenId));
            return revokeTokenId.getDomainId();
        }, REVOKE_TOKEN);
    }

    public SumPagedRep<RevokeToken> revokeTokens(String queryParam, String pageParam, String config) {
        return DomainRegistry.getRevokeTokenRepository().revokeTokensOfQuery(new RevokeTokenQuery(queryParam, pageParam, config));
    }

    @Transactional
    public void handleChange(ClientResourceCleanUpCompleted deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            //revoke deleted client token
            deserialize.getDomainIds().forEach(e -> DomainRegistry.getRevokeTokenService().revokeToken(e));
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(UserAuthorityChanged deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(UserDeleted deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }

    @Transactional
    public void handleChange(UserGetLocked deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }

    @Transactional
    public void handleChange(UserPasswordChanged deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(ClientAccessibilityRemoved deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                //revoke who is accessing this client's token
                Set<Client> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) query),
                        ClientQuery.queryByResource(new ClientId(deserialize.getDomainId().getDomainId()))
                );
                allByQuery.forEach(e -> {
                    DomainRegistry.getRevokeTokenService().revokeToken(e.getClientId());
                });
            return null;
        }, REVOKE_TOKEN);
    }

    @Transactional
    public void handleChange(ClientGrantTypeChanged deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(ClientTokenDetailChanged deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(ClientDeleted deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(ClientResourcesChanged deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
    @Transactional
    public void handleChange(ClientSecretChanged deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
            return null;
        }, REVOKE_TOKEN);
    }
}
