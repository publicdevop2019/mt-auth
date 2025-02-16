package com.mt.access.domain.model.token;

import static com.mt.access.domain.model.ticket.TicketInfo.PERMISSION_IDS;
import static com.mt.access.domain.model.ticket.TicketInfo.USER_ID;
import static com.mt.access.infrastructure.JwtCurrentUserService.TENANT_IDS;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.infrastructure.AppConstant;
import com.mt.access.infrastructure.JwtInfoProviderService;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {
    private static final String NOT_USED = "not_used";
    private static final String CLIENT_ID = "client_id";
    private static final String PROJECT_ID = "projectId";
    @Autowired
    private JwtInfoProviderService jwtInfoProviderService;

    public void grant(TokenGrantContext context) {
        JwtToken jwtToken = null;
        TokenGrantType grantType = context.getGrantType();
        ProjectId scope = context.getScope();
        if (grantType.equals(TokenGrantType.CLIENT_CREDENTIALS)
            ||
            grantType.equals(TokenGrantType.PASSWORD)
        ) {
            UserId userId =
                context.getLoginUser() == null ? null : context.getLoginUser().getUserId();
            Set<String> parsedScope =
                scope != null ? Collections.singleton(scope.getDomainId()) : Collections.emptySet();
            jwtToken = grantToken(context.getClient(), userId, parsedScope);
        } else if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
            jwtToken = grantRefreshToken(context.getClient(), context.getRefreshToken(),
                context.getViewTenantId());
        } else if (grantType.equals(TokenGrantType.AUTHORIZATION_CODE)) {
            jwtToken = grantAuthorizationCode(context.getClient(), context.getCode(),
                context.getRedirectUri());
        }
        context.setJwtToken(jwtToken);
    }

    private JwtToken grantToken(TokenGrantClient clientDetails,
                                @Nullable UserId userId,
                                Set<String> scope) {
        final boolean isClient = Checker.isNull(userId);
        final boolean hasRefresh =
            clientDetails.getAuthorizedGrantTypes().contains("refresh_token");
        ClientId clientId = new ClientId(clientDetails.getClientId());
        //for user & client
        ProjectId projectId = null;
        ProjectId viewTenantId = null;
        Set<PermissionId> totalPerm = new HashSet<>();
        Set<ProjectId> tenantIds = Collections.emptySet();
        if (isClient) {
            log.debug("grant client token");
            //for client
            RoleId roleId = clientDetails.getRoleId();
            Role byId =
                DomainRegistry.getRoleRepository().query(roleId);
            projectId = clientDetails.getProjectId();
            Set<PermissionId> comPerm =
                DomainRegistry.getCommonPermissionIdRepository().query(byId);
            Set<PermissionId> apiPerm = DomainRegistry.getApiPermissionIdRepository().query(byId);
            Set<PermissionId> extPerm =
                DomainRegistry.getExternalPermissionIdRepository().query(byId);
            totalPerm.addAll(comPerm);
            totalPerm.addAll(apiPerm);
            totalPerm.addAll(extPerm);
            log.debug("creating client token");
            return createJwtToken(
                projectId,
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getResourceIds(),
                scope,
                clientId,
                null,
                hasRefresh,
                totalPerm,
                Collections.emptySet(),
                null
            );
        } else {
            log.debug("grant user token");
            if (scope != null && scope.size() > 0
                &&
                !NOT_USED.equals(scope.stream().findFirst().get())) {
                //only one projectId allowed
                log.debug("get tenant project permission");
                Optional<String> first = scope.stream().findFirst();
                projectId = new ProjectId(first.get());
                UserRelation userRelation = createUserRelationIfNotExist(userId, projectId);
                totalPerm =
                    DomainRegistry.getComputePermissionService().compute(userRelation, null);
                projectId = userRelation.getProjectId();
            } else {
                log.debug("get user relation for root project");
                Optional<UserRelation> optional =
                    DomainRegistry.getUserRelationRepository()
                        .query(userId, new ProjectId(AppConstant.MAIN_PROJECT_ID));
                if (optional.isPresent()) {
                    UserRelation userRelation = optional.get();
                    Set<ProjectId> tenantIdSet =
                        DomainRegistry.getUserRelationTenantIdRepository().query(userRelation);
                    log.debug("auth user relation for token is {}", userRelation);
                    if (!tenantIdSet.isEmpty()) {
                        //for user with no projects
                        viewTenantId = DomainRegistry.getProjectService()
                            .getDefaultProject(tenantIdSet);
                    }
                    //get default project instead of all projects' permission to reduce header size
                    totalPerm =
                        DomainRegistry.getComputePermissionService()
                            .compute(userRelation, viewTenantId);
                    projectId = userRelation.getProjectId();
                    tenantIds = tenantIdSet;
                }
            }
            log.debug("creating token");
            return createJwtToken(
                projectId,
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getResourceIds(),
                scope,
                clientId,
                userId,
                hasRefresh,
                totalPerm,
                tenantIds,
                viewTenantId
            );
        }
    }

    private UserRelation createUserRelationIfNotExist(UserId userId, ProjectId projectId) {
        Optional<UserRelation> userRelation =
            DomainRegistry.getUserRelationRepository()
                .query(userId, projectId);
        if (userRelation.isEmpty()) {
            //auto assign default user role for target project
            UserRelation newRelation =
                ApplicationServiceRegistry.getUserRelationApplicationService()
                    .internalOnboardUserToTenant(userId, projectId);
            log.debug("new tenant user relation for token is {}", newRelation);
            return newRelation;
        } else {
            log.debug("tenant user relation for token is {}", userRelation.get());
            return userRelation.get();
        }
    }

    private JwtToken grantAuthorizationCode(TokenGrantClient client,
                                            String code,
                                            String redirectUrl) {
        Validator.notNull(code);
        Validator.notNull(redirectUrl);
        AuthorizeInfo authorizeInfo = DomainRegistry.getAuthorizationCodeRepository()
            .remove(code);
        if (Checker.isNull(authorizeInfo)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        //check redirect url
        Validator.equals(redirectUrl, authorizeInfo.getRedirectUri());
        //check client
        Validator.equals(client.getClientId(), authorizeInfo.getClientId().getDomainId());

        UserRelation userRelation =
            createUserRelationIfNotExist(authorizeInfo.getUserId(), authorizeInfo.getProjectId());
        Set<PermissionId> compute =
            DomainRegistry.getComputePermissionService().compute(userRelation, null);
        return createJwtToken(
            client.getProjectId(),
            client.getAccessTokenValiditySeconds(),
            0,
            client.getResourceIds(),
            authorizeInfo.getScope(),
            authorizeInfo.getClientId(),
            authorizeInfo.getUserId(),
            false,
            compute,
            Collections.emptySet(),
            null
        );
    }

    private JwtToken grantRefreshToken(TokenGrantClient client,
                                       String refreshToken, @Nullable ProjectId viewTenantId) {
        UserId userId = new UserId(JwtUtility.getUserId(refreshToken));
        Set<PermissionId> permissionIds = new HashSet<>();
        if (viewTenantId != null) {
            ProjectId originalViewTenantId =
                new ProjectId(JwtUtility.getField("viewTenantId", refreshToken));
            if (viewTenantId.equals(originalViewTenantId)) {
                permissionIds =
                    JwtUtility.getPermissionIds(refreshToken).stream().map(PermissionId::new)
                        .collect(Collectors.toSet());
            } else {
                log.debug("retrieve permissions for new view tenant id");
                Optional<UserRelation> optional =
                    DomainRegistry.getUserRelationRepository()
                        .query(userId, new ProjectId(AppConstant.MAIN_PROJECT_ID));
                if (optional.isPresent()) {
                    UserRelation userRelation = optional.get();
                    //get default project instead of all projects' permission to reduce header size
                    permissionIds =
                        DomainRegistry.getComputePermissionService()
                            .compute(userRelation, viewTenantId);
                }
            }
        } else {

            permissionIds =
                JwtUtility.getPermissionIds(refreshToken).stream().map(PermissionId::new)
                    .collect(Collectors.toSet());
        }

        String clientId = JwtUtility.getClientId(refreshToken);
        ClientId clientId1 = new ClientId(clientId);
        List<String> scope = JwtUtility.getScopes(refreshToken);
        List<String> collect = JwtUtility.getField("tenantIds", refreshToken);
        Set<ProjectId> tenantIds = collect.stream().map(ProjectId::new).collect(Collectors.toSet());
        String projectId = JwtUtility.getField("projectId", refreshToken);
        ProjectId projectId1 = new ProjectId(projectId);
        return createJwtToken(
            projectId1,
            client.getAccessTokenValiditySeconds(),
            client.getRefreshTokenValiditySeconds(),
            client.getResourceIds(),
            scope,
            clientId1,
            userId,
            true,
            permissionIds,
            tenantIds,
            viewTenantId
        );
    }

    private JwtToken createJwtToken(
        ProjectId projectId,
        int accessTokenSec,
        int refreshTokenSec,
        Collection<String> resourceIds,
        Collection<String> scope,
        ClientId clientId,
        UserId userId,
        boolean hasRefreshToken,
        Set<PermissionId> permissionIds,
        Set<ProjectId> tenantIds,
        ProjectId viewTenantId
    ) {
        JwtToken jwtToken = new JwtToken();

        Instant now = Instant.now();
        long iatMilli = now.toEpochMilli();
        Date iat = Date.from(now);
        long expMilli = iatMilli + (long) accessTokenSec * 1000;
        Date exp = Date.from(Instant.ofEpochMilli(expMilli));
        //for user & client
        String jwtId = UUID.randomUUID().toString();

        String jwtString = createJwtString(
            userId, resourceIds, iat, exp,
            projectId, jwtId, null,
            scope,
            clientId,
            permissionIds,
            tenantIds,
            viewTenantId
        );
        jwtToken.setSignedAccessToken(jwtString);

        jwtToken.setAccessTokenValidityInSecond(
            (long) accessTokenSec);
        jwtToken.setIssueAtMilli(iatMilli);
        jwtToken.setId(jwtId);
        jwtToken.setPermissionIds(permissionIds);
        jwtToken.setProjectId(projectId);
        jwtToken.setTenantIds(tenantIds);
        jwtToken.setUserId(userId);
        jwtToken.setViewTenantId(viewTenantId);
        if (hasRefreshToken) {
            //refresh token
            long refreshExpMilli =
                iatMilli + (long) refreshTokenSec * 1000;
            Date refreshExp = Date.from(Instant.ofEpochMilli(refreshExpMilli));
            String refreshJwtId = UUID.randomUUID().toString();

            String refreshJwtString = createJwtString(
                userId, resourceIds, iat, refreshExp,
                projectId, refreshJwtId, jwtId,
                scope,
                clientId,
                permissionIds,
                tenantIds,
                viewTenantId
            );
            jwtToken.setSignedRefreshToken(refreshJwtString);
        }
        return jwtToken;
    }

    private String createJwtString(
        @Nullable UserId userId,
        Collection<String> aud,
        Date iat,
        Date exp,
        @Nullable ProjectId projectId,
        String jwtId,
        @Nullable String referredTokenId,
        Collection<String> scope,
        ClientId clientId,
        Collection<PermissionId> permissionIds,
        Collection<ProjectId> tenantIds,
        @Nullable ProjectId viewTenantId
    ) {
        KeyPair keyPair = jwtInfoProviderService.getKeyPair();
        JWKSet publicKeys = jwtInfoProviderService.getPublicKeys();
        JWK jwk = publicKeys.getKeys().get(0);
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID())
                .type(JOSEObjectType.JWT)
                .build(),
            new JWTClaimsSet.Builder()
                .claim(USER_ID, userId == null ? null : userId.getDomainId())
                .claim("user_name",
                    userId == null ? null :
                        userId.getDomainId())//required to identify user token or client token
                .audience(new ArrayList<>(aud))
                .issueTime(iat)
                .expirationTime(exp)
                .claim(PROJECT_ID, projectId == null ? null : projectId.getDomainId())
                .claim("ati", referredTokenId)
                .claim("scope",
                    scope.isEmpty() ? List.of("not_used") : scope)//required for refresh
                .jwtID(jwtId)
                .claim(CLIENT_ID, clientId.getDomainId())
                .claim("viewTenantId", viewTenantId == null ? null : viewTenantId.getDomainId())
                .claim(PERMISSION_IDS,
                    permissionIds.stream().map(DomainId::getDomainId).collect(
                        Collectors.toSet()))
                .claim(TENANT_IDS, tenantIds.stream().map(DomainId::getDomainId).collect(
                    Collectors.toSet()))
                .build());
        try {
            signedJWT.sign(new RSASSASigner(keyPair.getPrivate()));
        } catch (JOSEException e) {
            throw new DefinedRuntimeException("unable to create jwt token", "1095",
                HttpResponseCode.INTERNAL_SERVER_ERROR, e);
        }
        return signedJWT.serialize();
    }

    public String authorize(String redirectUri, ClientId clientId, Set<String> scope,
                            ProjectId projectId,
                            Set<PermissionId> permissionIds, UserId userId) {
        AuthorizeInfo authorizeInfo = new AuthorizeInfo();
        authorizeInfo.setRedirectUri(redirectUri);
        authorizeInfo.setClientId(clientId);
        authorizeInfo.setScope(scope);
        authorizeInfo.setPermissionIds(permissionIds);
        authorizeInfo.setUserId(userId);
        authorizeInfo.setProjectId(projectId);
        String code = CommonDomainRegistry.getUniqueIdGeneratorService().idString();
        DomainRegistry.getAuthorizationCodeRepository()
            .store(code, authorizeInfo);
        return code;
    }
}
