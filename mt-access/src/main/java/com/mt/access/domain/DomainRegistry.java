package com.mt.access.domain;

import com.mt.access.domain.model.ComputePermissionService;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.CurrentUserService;
import com.mt.access.domain.model.EncryptionService;
import com.mt.access.domain.model.EndpointValidationService;
import com.mt.access.domain.model.NewUserService;
import com.mt.access.domain.model.PendingUserValidationService;
import com.mt.access.domain.model.PermissionCheckService;
import com.mt.access.domain.model.RemoteProxyService;
import com.mt.access.domain.model.UserValidationService;
import com.mt.access.domain.model.activation_code.ActivationCodeService;
import com.mt.access.domain.model.audit.AuditService;
import com.mt.access.domain.model.cache_profile.CacheProfileRepository;
import com.mt.access.domain.model.client.ClientRepository;
import com.mt.access.domain.model.client.ClientValidationService;
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
import com.mt.access.domain.model.operation_cool_down.OperationCoolDownRepository;
import com.mt.access.domain.model.organization.OrganizationRepository;
import com.mt.access.domain.model.pending_user.PendingUserRepository;
import com.mt.access.domain.model.pending_user.PendingUserService;
import com.mt.access.domain.model.permission.PermissionRepository;
import com.mt.access.domain.model.position.PositionRepository;
import com.mt.access.domain.model.project.ProjectRepository;
import com.mt.access.domain.model.proxy.ProxyService;
import com.mt.access.domain.model.report.DataProcessTrackerRepository;
import com.mt.access.domain.model.report.FormattedAccessRecordRepository;
import com.mt.access.domain.model.report.RawAccessRecordProcessService;
import com.mt.access.domain.model.report.RawAccessRecordRepository;
import com.mt.access.domain.model.report.ReportGenerateService;
import com.mt.access.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.access.domain.model.revoke_token.RevokeTokenService;
import com.mt.access.domain.model.role.RoleRepository;
import com.mt.access.domain.model.sub_request.SubRequestRepository;
import com.mt.access.domain.model.ticket.TicketService;
import com.mt.access.domain.model.user.LoginHistoryRepository;
import com.mt.access.domain.model.user.LoginInfoRepository;
import com.mt.access.domain.model.user.MfaCodeService;
import com.mt.access.domain.model.user.MfaService;
import com.mt.access.domain.model.user.PasswordResetTokenService;
import com.mt.access.domain.model.user.UserRepository;
import com.mt.access.domain.model.user.UserService;
import com.mt.access.domain.model.user_relation.UserRelationRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static ClientRepository clientRepository;
    @Getter
    private static UserRepository userRepository;
    @Getter
    private static PendingUserRepository pendingUserRepository;
    @Getter
    private static EndpointRepository endpointRepository;
    @Getter
    private static EndpointService endpointService;
    @Getter
    private static EncryptionService encryptionService;
    @Getter
    private static CurrentUserService currentUserService;
    @Getter
    private static PendingUserService pendingUserService;
    @Getter
    private static UserService userService;
    @Getter
    private static RevokeTokenService revokeTokenService;
    @Getter
    private static ActivationCodeService activationCodeService;
    @Getter
    private static PasswordResetTokenService passwordResetTokenService;
    @Getter
    private static RevokeTokenRepository revokeTokenRepository;
    @Getter
    private static EndpointValidationService endpointValidationService;
    @Getter
    private static ClientValidationService clientValidationService;
    @Getter
    private static PendingUserValidationService pendingUserValidationService;
    @Getter
    private static UserValidationService userValidationService;
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
    private static RoleRepository roleRepository;
    @Getter
    private static PermissionRepository permissionRepository;
    @Getter
    private static OrganizationRepository organizationRepository;
    @Getter
    private static PositionRepository positionRepository;
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
    private static MfaCodeService mfaCodeService;
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
    private static RawAccessRecordRepository rawAccessRecordRepository;
    @Getter
    private static SubRequestRepository subRequestRepository;
    @Getter
    private static ReportGenerateService reportGenerateService;
    @Getter
    private static FormattedAccessRecordRepository formattedAccessRecordRepository;
    @Getter
    private static RawAccessRecordProcessService rawAccessRecordProcessService;
    @Getter
    private static DataProcessTrackerRepository dataProcessTrackerRepository;

    @Autowired
    public void setRawAccessRecordProcessService(
        RawAccessRecordProcessService rawAccessRecordProcessService) {
        DomainRegistry.rawAccessRecordProcessService = rawAccessRecordProcessService;
    }

    @Autowired
    public void setDataProcessTrackerRepository(
        DataProcessTrackerRepository dataProcessTrackerRepository) {
        DomainRegistry.dataProcessTrackerRepository = dataProcessTrackerRepository;
    }

    @Autowired
    public void setFormattedAccessRecordRepository(
        FormattedAccessRecordRepository formattedAccessRecordRepository) {
        DomainRegistry.formattedAccessRecordRepository = formattedAccessRecordRepository;
    }

    @Autowired
    public void setReportGenerateService(ReportGenerateService reportGenerateService) {
        DomainRegistry.reportGenerateService = reportGenerateService;
    }

    @Autowired
    public void setSubRequestRepository(SubRequestRepository subRequestRepository) {
        DomainRegistry.subRequestRepository = subRequestRepository;
    }

    @Autowired
    public void setRawAccessRecordRepository(RawAccessRecordRepository rawAccessRecordRepository) {
        DomainRegistry.rawAccessRecordRepository = rawAccessRecordRepository;
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
    public void setMfaCodeService(MfaCodeService mfaCodeService) {
        DomainRegistry.mfaCodeService = mfaCodeService;
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
    public void setPositionRepository(PositionRepository repository) {
        DomainRegistry.positionRepository = repository;
    }

    @Autowired
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        DomainRegistry.organizationRepository = organizationRepository;
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
    public void setUserValidationService(UserValidationService userValidationService) {
        DomainRegistry.userValidationService = userValidationService;
    }

    @Autowired
    public void setPendingUserValidationService(
        PendingUserValidationService pendingUserValidationService) {
        DomainRegistry.pendingUserValidationService = pendingUserValidationService;
    }

    @Autowired
    public void setClientValidationService(ClientValidationService clientValidationService) {
        DomainRegistry.clientValidationService = clientValidationService;
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
    public void setActivationCodeService(ActivationCodeService activationCodeService) {
        DomainRegistry.activationCodeService = activationCodeService;
    }

    @Autowired
    public void setEndpointRepository(EndpointRepository endpointRepository) {
        DomainRegistry.endpointRepository = endpointRepository;
    }

    @Autowired
    public void setPasswordResetTokenService(PasswordResetTokenService passwordResetTokenService) {
        DomainRegistry.passwordResetTokenService = passwordResetTokenService;
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
    public void setPendingUserRepo(PendingUserRepository pendingUserRepo) {
        DomainRegistry.pendingUserRepository = pendingUserRepo;
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
    public void setPendingUserService(PendingUserService pendingUserService) {
        DomainRegistry.pendingUserService = pendingUserService;
    }

}
