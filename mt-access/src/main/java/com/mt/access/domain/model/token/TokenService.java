package com.mt.access.domain.model.token;

import static com.mt.access.domain.model.ticket.TicketInfo.PERMISSION_IDS;
import static com.mt.access.domain.model.ticket.TicketInfo.USER_ID;
import static com.mt.access.infrastructure.JwtCurrentUserService.TENANT_IDS;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.representation.ClientSpringOAuth2Representation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.infrastructure.AppConstant;
import com.mt.access.infrastructure.JwtInfoProviderService;
import com.mt.common.domain.model.domain_event.DomainId;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyPair;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {
    @Autowired
    private OAuth2RequestFactory factory;
    @Autowired
    private JwtAccessTokenConverter converter;
    @Autowired
    JwtInfoProviderService jwtInfoProviderService;
    private static final String NOT_USED = "not_used";
    private static final String CLIENT_ID = "client_id";
    private static final String PROJECT_ID = "projectId";

    public JwtToken grant(Map<String, String> parameters,
                          ClientDetails clientDetails, UserDetails userDetails) {
        String scope = parameters.get("scope");
        if ("client_credential".equalsIgnoreCase(parameters.get("grant_type"))) {
            JwtToken token = grantClientCredential(clientDetails);
            return token;
        } else if ("password".equalsIgnoreCase(parameters.get("grant_type"))) {
            JwtToken token =
                grantPassword(clientDetails, userDetails,
                    scope != null ? Collections.singleton(scope) : Collections.emptySet());
            return token;

        } else if ("refresh_token".equalsIgnoreCase(parameters.get("grant_type"))) {
            JwtToken token = grantRefreshToken(clientDetails);
            return token;

        } else if ("authorization_code".equalsIgnoreCase(parameters.get("grant_type"))) {
            JwtToken token = grantAuthorizationCode(clientDetails);
            return token;
        } else {
            return null;
        }
    }

    private JwtToken grantPassword(ClientDetails clientDetails, UserDetails userDetails,
                                   Set<String> scope) {
        String username = userDetails.getUsername();
        ClientSpringOAuth2Representation clientDetail =
            (ClientSpringOAuth2Representation) clientDetails;
        JWKSet publicKeys = jwtInfoProviderService.getPublicKeys();
        KeyPair keyPair = jwtInfoProviderService.getKeyPair();
        JWK jwk = publicKeys.getKeys().get(0);
        Instant now = Instant.now();
        long iatMilli = now.toEpochMilli();
        Date iat = Date.from(now);
        long expMilli = iatMilli + (long) clientDetails.getAccessTokenValiditySeconds() * 1000;
        Date exp = Date.from(Instant.ofEpochMilli(expMilli));
        UserId userId = new UserId(username);
        //for user
        ProjectId projectId = null;
        Set<PermissionId> permissionIds = Collections.emptySet();
        Set<ProjectId> tenantIds = Collections.emptySet();
        if (scope != null && scope.size() > 0
            &&
            !NOT_USED.equals(scope.stream().findFirst().get())) {
            //only one projectId allowed
            //get tenant project permission
            Optional<String> first = scope.stream().findFirst();
            projectId = new ProjectId(first.get());
            Optional<UserRelation> userRelation =
                ApplicationServiceRegistry.getUserRelationApplicationService()
                    .query(userId, projectId);
            if (userRelation.isEmpty()) {
                //auto assign default user role for target project
                UserRelation newRelation =
                    ApplicationServiceRegistry.getUserRelationApplicationService()
                        .internalOnboardUserToTenant(userId, projectId);
                log.debug("new tenant user relation for token is {}", newRelation);
                permissionIds =
                    DomainRegistry.getComputePermissionService().compute(newRelation);
                projectId = newRelation.getProjectId();
            } else {
                log.debug("tenant user relation for token is {}", userRelation.get());
                permissionIds =
                    DomainRegistry.getComputePermissionService().compute(userRelation.get());
                projectId = userRelation.get().getProjectId();
            }
        } else {
            //get auth project permission and user tenant projects
            Optional<UserRelation> userRelation =
                ApplicationServiceRegistry.getUserRelationApplicationService()
                    .query(userId, new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
            userRelation.ifPresent(
                relation -> log.debug("auth user relation for token is {}", relation));
            if (userRelation.isPresent()) {
                UserRelation userRelation1 = userRelation.get();
                permissionIds =
                    DomainRegistry.getComputePermissionService().compute(userRelation1);
                projectId = userRelation1.getProjectId();
                if (userRelation1.getTenantIds() != null) {
                    tenantIds = userRelation1.getTenantIds();
                }
            }
        }
        String accessTokenId = UUID.randomUUID().toString();
        JwtToken jwtToken = new JwtToken();
        SignedJWT signedAccessToken = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).type(JOSEObjectType.JWT)
                .build(),
            new JWTClaimsSet.Builder()
                .claim(USER_ID, username)
                .claim("user_name", username)//required to identify user token or client token
                .claim("aud", new ArrayList<>(
                    clientDetails.getResourceIds()))//avoid single value converted to list
                .issueTime(iat)
                .claim("scope", scope.isEmpty() ? List.of("not_used") : scope)//required for refresh
                .expirationTime(exp)
                .claim(PROJECT_ID, projectId == null ? null : projectId.getDomainId())
                .jwtID(accessTokenId)
                .claim(CLIENT_ID, clientDetails.getClientId())
                .claim(PERMISSION_IDS, permissionIds.stream().map(DomainId::getDomainId).collect(
                    Collectors.toSet()))
                .claim(TENANT_IDS, tenantIds.stream().map(DomainId::getDomainId).collect(
                    Collectors.toSet()))
                .build());
        try {
            signedAccessToken.sign(new RSASSASigner(keyPair.getPrivate()));
        } catch (JOSEException e) {
            //TODO add custom error code
            log.error("error during generating token", e);
        }
        jwtToken.setSignedAccessToken(signedAccessToken.serialize());

        if (clientDetail.getAuthorizedGrantTypes().contains("refresh_token")) {
            //refresh token
            long refreshExpMilli =
                iatMilli + (long) clientDetails.getRefreshTokenValiditySeconds() * 1000;
            Date refreshExp = Date.from(Instant.ofEpochMilli(refreshExpMilli));
            SignedJWT signedRefreshToken = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID())
                    .type(JOSEObjectType.JWT)
                    .build(),
                new JWTClaimsSet.Builder()
                    .claim(USER_ID, username)
                    .claim("user_name", username)//required to identify user token or client token
                    .claim("aud", new ArrayList<>(
                        clientDetails.getResourceIds()))//avoid single value converted to list
                    .issueTime(iat)
                    .expirationTime(refreshExp)
                    .claim(PROJECT_ID, projectId == null ? null : projectId.getDomainId())
                    .claim("ati", accessTokenId)
                    .claim("scope", scope.isEmpty() ? List.of("not_used") : scope)//required for refresh
                    .jwtID(UUID.randomUUID().toString())
                    .claim(CLIENT_ID, clientDetails.getClientId())
                    .claim(PERMISSION_IDS,
                        permissionIds.stream().map(DomainId::getDomainId).collect(
                            Collectors.toSet()))
                    .claim(TENANT_IDS, tenantIds.stream().map(DomainId::getDomainId).collect(
                        Collectors.toSet()))
                    .build());
            try {
                signedRefreshToken.sign(new RSASSASigner(keyPair.getPrivate()));
            } catch (JOSEException e) {
                //TODO add custom error code
                log.error("error during generating token", e);
            }
            jwtToken.setSignedRefreshToken(signedRefreshToken.serialize());
        }


        jwtToken.setAccessTokenValidityInSecond(
            (long) clientDetails.getAccessTokenValiditySeconds());
        jwtToken.setIssueAtMilli(iatMilli);
        jwtToken.setId(accessTokenId);
        jwtToken.setPermissionIds(permissionIds);
        jwtToken.setProjectId(projectId);
        jwtToken.setTenantIds(tenantIds);
        jwtToken.setUserId(userId);
        return jwtToken;
    }

    private JwtToken grantAuthorizationCode(ClientDetails tokenRequest) {
        return null;
    }

    private JwtToken grantRefreshToken(ClientDetails tokenRequest) {
        return null;
    }


    private JwtToken grantClientCredential(ClientDetails tokenRequest) {
        return null;

    }
}
