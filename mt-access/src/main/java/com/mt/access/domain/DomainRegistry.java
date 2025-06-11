package com.mt.access.domain;

import com.mt.access.domain.model.CacheProfileValidationService;
import com.mt.access.domain.model.ClientExternalResourceService;
import com.mt.access.domain.model.ComputePermissionService;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.CurrentUserService;
import com.mt.access.domain.model.EncryptionService;
import com.mt.access.domain.model.EndpointValidationService;
import com.mt.access.domain.model.MfaService;
import com.mt.access.domain.model.NewUserService;
import com.mt.access.domain.model.PermissionCheckService;
import com.mt.access.domain.model.PwdResetService;
import com.mt.access.domain.model.RemoteProxyService;
import com.mt.access.domain.model.RevokeTokenValidationService;
import com.mt.access.domain.model.TokenGrantService;
import com.mt.access.domain.model.VerificationCodeService;
import com.mt.access.domain.model.audit.AuditRecordRepository;
import com.mt.access.domain.model.audit.AuditService;
import com.mt.access.domain.model.cache_profile.CacheControlRepository;
import com.mt.access.domain.model.cache_profile.CacheProfileRepository;
import com.mt.access.domain.model.client.ClientExternalResourceRepository;
import com.mt.access.domain.model.client.ClientGrantTypeRepository;
import com.mt.access.domain.model.client.ClientRedirectUrlRepository;
import com.mt.access.domain.model.client.ClientRepository;
import com.mt.access.domain.model.client.ClientResourceRepository;
import com.mt.access.domain.model.cors_profile.CorsAllowedHeaderRepository;
import com.mt.access.domain.model.cors_profile.CorsExposedHeaderRepository;
import com.mt.access.domain.model.cors_profile.CorsOriginRepository;
import com.mt.access.domain.model.cors_profile.CorsProfileRepository;
import com.mt.access.domain.model.cross_domain_validation.ValidationResultRepository;
import com.mt.access.domain.model.endpoint.EndpointRepository;
import com.mt.access.domain.model.endpoint.EndpointService;
import com.mt.access.domain.model.image.ImageRepository;
import com.mt.access.domain.model.notification.EmailNotificationService;
import com.mt.access.domain.model.notification.NotificationRepository;
import com.mt.access.domain.model.notification.SmsNotificationService;
import com.mt.access.domain.model.notification.WsPushNotificationService;
import com.mt.access.domain.model.operation_cool_down.CoolDownService;
import com.mt.access.domain.model.permission.LinkedApiPermissionIdRepository;
import com.mt.access.domain.model.permission.PermissionRepository;
import com.mt.access.domain.model.permission.PermissionService;
import com.mt.access.domain.model.project.ProjectRepository;
import com.mt.access.domain.model.project.ProjectService;
import com.mt.access.domain.model.proxy.ProxyService;
import com.mt.access.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.access.domain.model.revoke_token.RevokeTokenService;
import com.mt.access.domain.model.role.ApiPermissionIdRepository;
import com.mt.access.domain.model.role.CommonPermissionIdRepository;
import com.mt.access.domain.model.role.ExternalPermissionIdRepository;
import com.mt.access.domain.model.role.RoleRepository;
import com.mt.access.domain.model.role.RoleValidationService;
import com.mt.access.domain.model.instance.InstanceService;
import com.mt.access.domain.model.sub_request.SubRequestRepository;
import com.mt.access.domain.model.temporary_code.TemporaryCodeRepository;
import com.mt.access.domain.model.temporary_code.TemporaryCodeService;
import com.mt.access.domain.model.ticket.TicketService;
import com.mt.access.domain.model.token.AuthorizationCodeRepository;
import com.mt.access.domain.model.token.TokenService;
import com.mt.access.domain.model.user.LoginHistoryRepository;
import com.mt.access.domain.model.user.LoginInfoRepository;
import com.mt.access.domain.model.user.MfaCodeGenerator;
import com.mt.access.domain.model.user.PwdResetCodeGenerator;
import com.mt.access.domain.model.user.UserRelationRepository;
import com.mt.access.domain.model.user.UserRelationRoleIdRepository;
import com.mt.access.domain.model.user.UserRelationTenantIdRepository;
import com.mt.access.domain.model.user.UserRepository;
import com.mt.access.domain.model.user.UserService;
import com.mt.access.domain.model.verification_code.VerificationCodeGenerator;
import com.mt.access.infrastructure.operation_cool_down.OperationCoolDownRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainRegistry {
    @Getter
    private static ClientRepository clientRepository;
    @Getter
    private static ClientResourceRepository clientResourceRepository;
    @Getter
    private static ClientExternalResourceService clientExternalResourceService;
    @Getter
    private static ClientExternalResourceRepository clientExternalResourceRepository;
    @Getter
    private static ClientRedirectUrlRepository clientRedirectUrlRepository;
    @Getter
    private static ClientGrantTypeRepository clientGrantTypeRepository;
    @Getter
    private static UserRepository userRepository;
    @Getter
    private static TemporaryCodeRepository temporaryCodeRepository;
    @Getter
    private static EndpointRepository endpointRepository;
    @Getter
    private static EndpointService endpointService;
    @Getter
    private static EncryptionService encryptionService;
    @Getter
    private static CurrentUserService currentUserService;
    @Getter
    private static VerificationCodeService verificationCodeService;
    @Getter
    private static UserService userService;
    @Getter
    private static RevokeTokenService revokeTokenService;
    @Getter
    private static VerificationCodeGenerator loginCodeGenerator;
    @Getter
    private static PwdResetCodeGenerator pwdResetCodeGenerator;
    @Getter
    private static RevokeTokenRepository revokeTokenRepository;
    @Getter
    private static EndpointValidationService endpointValidationService;
    @Getter
    private static RevokeTokenValidationService revokeTokenValidationService;
    @Getter
    private static TicketService ticketService;
    @Getter
    private static NewUserService newUserService;
    @Getter
    private static CorsProfileRepository corsProfileRepository;
    @Getter
    private static CacheProfileRepository cacheProfileRepository;
    @Getter
    private static RemoteProxyService remoteProxyService;
    @Getter
    private static ProjectRepository projectRepository;
    @Getter
    private static ProjectService projectService;
    @Getter
    private static RoleRepository roleRepository;
    @Getter
    private static PermissionRepository permissionRepository;
    @Getter
    private static UserRelationRepository userRelationRepository;
    @Getter
    private static ComputePermissionService computePermissionService;
    @Getter
    private static PermissionCheckService permissionCheckService;
    @Getter
    private static NotificationRepository notificationRepository;
    @Getter
    private static ProxyService proxyService;
    @Getter
    private static MfaCodeGenerator mfaCodeGeneratorService;
    @Getter
    private static ImageRepository imageRepository;
    @Getter
    private static LoginInfoRepository loginInfoRepository;
    @Getter
    private static LoginHistoryRepository loginHistoryRepository;
    @Getter
    private static ValidationResultRepository validationResultRepository;
    @Getter
    private static CrossDomainValidationService crossDomainValidationService;
    @Getter
    private static SmsNotificationService smsNotificationService;
    @Getter
    private static EmailNotificationService emailNotificationService;
    @Getter
    private static WsPushNotificationService wsPushNotificationService;
    @Getter
    private static MfaService mfaService;
    @Getter
    private static AuditService auditService;
    @Getter
    private static OperationCoolDownRepository operationCoolDownRepository;
    @Getter
    private static CoolDownService coolDownService;
    @Getter
    private static SubRequestRepository subRequestRepository;
    @Getter
    private static AuditRecordRepository auditRepository;
    @Getter
    private static CacheProfileValidationService cacheProfileValidationService;
    @Getter
    private static RoleValidationService roleValidationService;
    @Getter
    private static PermissionService permissionService;
    @Getter
    private static TokenService tokenService;
    @Getter
    private static AuthorizationCodeRepository authorizationCodeRepository;
    @Getter
    private static TemporaryCodeService temporaryCodeService;
    @Getter
    private static PwdResetService pwdResetService;
    @Getter
    private static TokenGrantService tokenGrantService;
    @Getter
    private static CacheControlRepository cacheControlRepository;
    @Getter
    private static CorsAllowedHeaderRepository corsAllowedHeaderRepository;
    @Getter
    private static CorsExposedHeaderRepository corsExposedHeaderRepository;
    @Getter
    private static CorsOriginRepository corsOriginRepository;
    @Getter
    private static LinkedApiPermissionIdRepository linkedApiPermissionIdRepository;
    @Getter
    private static ApiPermissionIdRepository apiPermissionIdRepository;
    @Getter
    private static CommonPermissionIdRepository commonPermissionIdRepository;
    @Getter
    private static ExternalPermissionIdRepository externalPermissionIdRepository;
    @Getter
    private static UserRelationRoleIdRepository userRelationRoleIdRepository;
    @Getter
    private static UserRelationTenantIdRepository userRelationTenantIdRepository;
    @Getter
    private static InstanceService instanceService;

    @Autowired
    public void setInstanceService(InstanceService services) {
        DomainRegistry.instanceService = services;
    }

    @Autowired
    public void setUserRelationRoleIdRepository(UserRelationRoleIdRepository services) {
        DomainRegistry.userRelationRoleIdRepository = services;
    }

    @Autowired
    public void setUserRelationTenantIdRepository(UserRelationTenantIdRepository services) {
        DomainRegistry.userRelationTenantIdRepository = services;
    }

    @Autowired
    public void setApiPermissionIdRepository(ApiPermissionIdRepository services) {
        DomainRegistry.apiPermissionIdRepository = services;
    }

    @Autowired
    public void setCommonPermissionIdRepository(CommonPermissionIdRepository services) {
        DomainRegistry.commonPermissionIdRepository = services;
    }

    @Autowired
    public void setExternalPermissionIdRepository(ExternalPermissionIdRepository services) {
        DomainRegistry.externalPermissionIdRepository = services;
    }

    @Autowired
    public void setLinkedApiPermissionIdRepository(LinkedApiPermissionIdRepository services) {
        DomainRegistry.linkedApiPermissionIdRepository = services;
    }

    @Autowired
    public void setCorsAllowedHeaderRepository(CorsAllowedHeaderRepository services) {
        DomainRegistry.corsAllowedHeaderRepository = services;
    }

    @Autowired
    public void setCorsExposedHeaderRepository(CorsExposedHeaderRepository services) {
        DomainRegistry.corsExposedHeaderRepository = services;
    }

    @Autowired
    public void setCorsOriginRepository(CorsOriginRepository services) {
        DomainRegistry.corsOriginRepository = services;
    }

    @Autowired
    public void setCacheControlRepository(CacheControlRepository services) {
        DomainRegistry.cacheControlRepository = services;
    }

    @Autowired
    public void setClientGrantTypeRepository(ClientGrantTypeRepository services) {
        DomainRegistry.clientGrantTypeRepository = services;
    }

    @Autowired
    public void setTokenGrantService(TokenGrantService services) {
        DomainRegistry.tokenGrantService = services;
    }

    @Autowired
    public void setClientRedirectUrlRepository(ClientRedirectUrlRepository services) {
        DomainRegistry.clientRedirectUrlRepository = services;
    }

    @Autowired
    public void setClientExternalResourceRepository(ClientExternalResourceRepository services) {
        DomainRegistry.clientExternalResourceRepository = services;
    }

    @Autowired
    public void setClientExternalResourceService(ClientExternalResourceService services) {
        DomainRegistry.clientExternalResourceService = services;
    }

    @Autowired
    public void setClientResourceRepository(ClientResourceRepository repository) {
        DomainRegistry.clientResourceRepository = repository;
    }

    @Autowired
    public void setRevokeTokenValidationService(RevokeTokenValidationService services) {
        DomainRegistry.revokeTokenValidationService = services;
    }

    @Autowired
    public void setPwdResetService(PwdResetService services) {
        DomainRegistry.pwdResetService = services;
    }

    @Autowired
    public void setTemporaryCodeService(TemporaryCodeService services) {
        DomainRegistry.temporaryCodeService = services;
    }

    @Autowired
    public void setRedisAuthorizationCodeServices(AuthorizationCodeRepository services) {
        DomainRegistry.authorizationCodeRepository = services;
    }

    @Autowired
    public void setProjectService(ProjectService services) {
        DomainRegistry.projectService = services;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        DomainRegistry.tokenService = tokenService;
    }

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        DomainRegistry.permissionService = permissionService;
    }

    @Autowired
    public void setRoleValidationService(RoleValidationService roleValidationService) {
        DomainRegistry.roleValidationService = roleValidationService;
    }

    @Autowired
    public void setCacheProfileValidationService(CacheProfileValidationService service) {
        DomainRegistry.cacheProfileValidationService = service;
    }

    @Autowired
    public void setAuditRepository(AuditRecordRepository auditRepository) {
        DomainRegistry.auditRepository = auditRepository;
    }

    @Autowired
    public void setSubRequestRepository(SubRequestRepository subRequestRepository) {
        DomainRegistry.subRequestRepository = subRequestRepository;
    }

    @Autowired
    public void setCoolDownService(CoolDownService coolDownService) {
        DomainRegistry.coolDownService = coolDownService;
    }

    @Autowired
    public void setAuditService(AuditService auditService) {
        DomainRegistry.auditService = auditService;
    }

    @Autowired
    public void setOperationCoolDownRepository(
        OperationCoolDownRepository operationCoolDownRepository) {
        DomainRegistry.operationCoolDownRepository = operationCoolDownRepository;
    }

    @Autowired
    public void setEmailNotificationService(EmailNotificationService emailNotificationService) {
        DomainRegistry.emailNotificationService = emailNotificationService;
    }

    @Autowired
    public void setMfaService(MfaService mfaService) {
        DomainRegistry.mfaService = mfaService;
    }

    @Autowired
    public void setWsPushNotificationService(WsPushNotificationService wsPushNotificationService) {
        DomainRegistry.wsPushNotificationService = wsPushNotificationService;
    }

    @Autowired
    public void setSmsNotificationService(SmsNotificationService smsNotificationService) {
        DomainRegistry.smsNotificationService = smsNotificationService;
    }

    @Autowired
    public void setMfaCodeGeneratorService(MfaCodeGenerator mfaCodeGeneratorService) {
        DomainRegistry.mfaCodeGeneratorService = mfaCodeGeneratorService;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        DomainRegistry.imageRepository = imageRepository;
    }

    @Autowired
    public void setProxyService(ProxyService proxyService) {
        DomainRegistry.proxyService = proxyService;
    }

    @Autowired
    public void setCrossDomainValidationService(
        CrossDomainValidationService crossDomainValidationService) {
        DomainRegistry.crossDomainValidationService = crossDomainValidationService;
    }

    @Autowired
    public void setValidationResultRepository(
        ValidationResultRepository validationResultRepository) {
        DomainRegistry.validationResultRepository = validationResultRepository;
    }

    @Autowired
    public void setLoginHistoryRepository(LoginHistoryRepository loginHistoryRepository) {
        DomainRegistry.loginHistoryRepository = loginHistoryRepository;
    }

    @Autowired
    public void setLoginInfoRepository(LoginInfoRepository loginInfoRepository) {
        DomainRegistry.loginInfoRepository = loginInfoRepository;
    }

    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository) {
        DomainRegistry.notificationRepository = notificationRepository;
    }

    @Autowired
    public void setPermissionCheckService(PermissionCheckService permissionCheckService) {
        DomainRegistry.permissionCheckService = permissionCheckService;
    }

    @Autowired
    public void setComputePermissionService(ComputePermissionService computePermissionService) {
        DomainRegistry.computePermissionService = computePermissionService;
    }

    @Autowired
    public void setUserRelationRepository(UserRelationRepository repository) {
        DomainRegistry.userRelationRepository = repository;
    }


    @Autowired
    public void setPermissionRepository(PermissionRepository permissionRepository) {
        DomainRegistry.permissionRepository = permissionRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        DomainRegistry.roleRepository = roleRepository;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        DomainRegistry.projectRepository = projectRepository;
    }

    @Autowired
    public void setRemoteProxyService(RemoteProxyService remoteProxyService) {
        DomainRegistry.remoteProxyService = remoteProxyService;
    }

    @Autowired
    public void setCacheProfileRepository(CacheProfileRepository cacheProfileRepository) {
        DomainRegistry.cacheProfileRepository = cacheProfileRepository;
    }

    @Autowired
    public void setNewUserService(NewUserService newUserService) {
        DomainRegistry.newUserService = newUserService;
    }

    @Autowired
    public void setCorsProfileRepository(CorsProfileRepository corsProfileRepository) {
        DomainRegistry.corsProfileRepository = corsProfileRepository;
    }

    @Autowired
    public void setTicketService(TicketService ticketService) {
        DomainRegistry.ticketService = ticketService;
    }

    @Autowired
    public void setEndpointValidationService(EndpointValidationService endpointValidationService) {
        DomainRegistry.endpointValidationService = endpointValidationService;
    }

    @Autowired
    public void setEndpointService(EndpointService endpointService) {
        DomainRegistry.endpointService = endpointService;
    }

    @Autowired
    public void setRevokeTokenRepository(RevokeTokenRepository revokeTokenRepository) {
        DomainRegistry.revokeTokenRepository = revokeTokenRepository;
    }

    @Autowired
    public void setLoginCodeGenerator(VerificationCodeGenerator loginCodeGenerator) {
        DomainRegistry.loginCodeGenerator = loginCodeGenerator;
    }

    @Autowired
    public void setEndpointRepository(EndpointRepository endpointRepository) {
        DomainRegistry.endpointRepository = endpointRepository;
    }

    @Autowired
    public void setPwdResetCodeGenerator(
        PwdResetCodeGenerator pwdResetTokenGeneratorService) {
        DomainRegistry.pwdResetCodeGenerator = pwdResetTokenGeneratorService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        DomainRegistry.userService = userService;
    }

    @Autowired
    public void setRevokeTokenService(RevokeTokenService revokeTokenService) {
        DomainRegistry.revokeTokenService = revokeTokenService;
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        DomainRegistry.clientRepository = clientRepository;
    }

    @Autowired
    public void setBizUserRepo(UserRepository bizUserRepo) {
        DomainRegistry.userRepository = bizUserRepo;
    }

    @Autowired
    public void setPendingUserRepo(TemporaryCodeRepository pendingUserRepo) {
        DomainRegistry.temporaryCodeRepository = pendingUserRepo;
    }

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        DomainRegistry.encryptionService = encryptionService;
    }

    @Autowired
    public void setCurrentUserService(CurrentUserService currentUserService) {
        DomainRegistry.currentUserService = currentUserService;
    }

    @Autowired
    public void setVerificationCodeService(VerificationCodeService verificationCodeService) {
        DomainRegistry.verificationCodeService = verificationCodeService;
    }

}
