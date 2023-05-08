package com.mt.access.application.revoke_token;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.event.ClientAccessibilityRemoved;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted;
import com.mt.access.domain.model.client.event.ClientResourcesChanged;
import com.mt.access.domain.model.client.event.ClientSecretChanged;
import com.mt.access.domain.model.client.event.ClientTokenDetailChanged;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;
import com.mt.access.domain.model.revoke_token.RevokeTokenQuery;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserDeleted;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RevokeTokenApplicationService {

    private static final String REVOKE_TOKEN = "RevokeToken";


    public void revoke(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                DomainRegistry.getRevokeTokenRepository().add(new RevokeToken(revokeTokenId));
                return null;
            }, REVOKE_TOKEN);
    }


    public void internalRevoke(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                DomainRegistry.getRevokeTokenRepository().add(new RevokeToken(revokeTokenId));
                return null;
            }, REVOKE_TOKEN);
    }

    public SumPagedRep<RevokeToken> query(String queryParam, String pageParam,
                                          String config) {
        return DomainRegistry.getRevokeTokenRepository()
            .revokeTokensOfQuery(new RevokeTokenQuery(queryParam, pageParam, config));
    }

    public void handle(ClientResourceCleanUpCompleted deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                //revoke deleted client token
                deserialize.getDomainIds()
                    .forEach(e -> DomainRegistry.getRevokeTokenService().revokeToken(e));
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(UserAuthorityChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(UserDeleted deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(UserGetLocked deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(UserPasswordChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientAccessibilityRemoved deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                //revoke who is accessing this client's token
                Set<Client> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getClientRepository().query(query),
                    ClientQuery
                        .queryByResource(new ClientId(deserialize.getDomainId().getDomainId()))
                );
                allByQuery.forEach(e -> {
                    DomainRegistry.getRevokeTokenService().revokeToken(e.getClientId());
                });
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientGrantTypeChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientTokenDetailChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientDeleted deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientResourcesChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientSecretChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }
}
