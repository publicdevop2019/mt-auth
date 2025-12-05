package com.mt.access.application.revoke_token;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.access.domain.model.client.event.ClientSecretChanged;
import com.mt.access.domain.model.client.event.ClientTokenDetailChanged;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;
import com.mt.access.domain.model.revoke_token.RevokeTokenQuery;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Service;

@Service
public class RevokeTokenApplicationService {

    private static final String REVOKE_TOKEN = "RevokeToken";


    public void revoke(RevokeTokenCreateCommand command, String changeId) {
        RevokeTokenId revokeTokenId = new RevokeTokenId(command.getId());
        DomainRegistry.getRevokeTokenValidationService().checkToken(revokeTokenId);
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
            .query(new RevokeTokenQuery(queryParam, pageParam, config));
    }

    public void handle(UserAuthorityChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(UserGetLocked deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(UserPasswordChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientGrantTypeChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientTokenDetailChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientDeleted deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }

    public void handle(ClientSecretChanged deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                DomainRegistry.getRevokeTokenService().revokeToken(deserialize.getDomainId());
                return null;
            }, REVOKE_TOKEN);
    }
}
