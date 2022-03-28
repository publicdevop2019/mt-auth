package com.mt.access.infrastructure;

import static com.mt.access.domain.model.ticket.TicketInfo.AUD;
import static com.mt.access.domain.model.ticket.TicketInfo.CLIENT_ID;
import static com.mt.access.domain.model.ticket.TicketInfo.PERMISSION_IDS;
import static com.mt.access.domain.model.ticket.TicketInfo.USER_ID;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.ticket.SignedTicket;
import com.mt.access.domain.model.ticket.TicketInfo;
import com.mt.access.domain.model.ticket.TicketService;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyPair;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTicketService implements TicketService {
    @Autowired
    JwtInfoProviderService jwtInfoProviderService;

    @Override
    public SignedTicket create(UserId userId, ClientId clientId, ClientId aud) {
        TicketInfo ticket = TicketInfo.create(userId, clientId, aud);
        return encrypt(ticket);
    }

    private SignedTicket encrypt(TicketInfo ticket) {
        JWKSet publicKeys = jwtInfoProviderService.getPublicKeys();
        KeyPair keyPair = jwtInfoProviderService.getKeyPair();

        JWK jwk = publicKeys.getKeys().get(0);
        Set<Endpoint> allWsEndpoint = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e),
                EndpointQuery.websocketQuery());
        Set<String> allWsPermission =
            allWsEndpoint.stream().filter(e -> e.getPermissionId() != null)
                .map(e -> e.getPermissionId().getDomainId()).collect(Collectors.toSet());
        Set<String> currentPermission = DomainRegistry.getCurrentUserService().userPermissionIds();
        Set<String> wsRelatedPermission =
            currentPermission.stream().filter(allWsPermission::contains)
                .collect(Collectors.toSet());
        SignedJWT signedJwt = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).build(),
            new JWTClaimsSet.Builder()
                .subject("ticket")
                .issueTime(new Date())
                .expirationTime(new Date(ticket.getExp()))
                .claim(USER_ID, ticket.getUserId().getDomainId())
                .claim(CLIENT_ID, ticket.getClientId().getDomainId())
                .claim(AUD, ticket.getAud().getDomainId())
                .claim(PERMISSION_IDS, wsRelatedPermission)
                .build());

        try {
            signedJwt.sign(new RSASSASigner(keyPair.getPrivate()));
        } catch (JOSEException e) {
            log.error("error during generating ticket", e);
        }
        return new SignedTicket(signedJwt.serialize());
    }
}
