package com.mt.access.application;

import com.mt.access.application.audit.AuditApplicationService;
import com.mt.access.application.cache_profile.CacheProfileApplicationService;
import com.mt.access.application.client.ClientApplicationService;
import com.mt.access.application.cors_profile.CorsProfileApplicationService;
import com.mt.access.application.cross_domain_validation.CrossDomainValidationApplicationService;
import com.mt.access.application.endpoint.EndpointApplicationService;
import com.mt.access.application.image.ImageApplicationService;
import com.mt.access.application.notification.NotificationApplicationService;
import com.mt.access.application.verification_code.VerificationCodeApplicationService;
import com.mt.access.application.permission.PermissionApplicationService;
import com.mt.access.application.project.ProjectApplicationService;
import com.mt.access.application.proxy.ProxyApplicationService;
import com.mt.access.application.registry.RegistryApplicationService;
import com.mt.access.application.report.ReportApplicationService;
import com.mt.access.application.revoke_token.RevokeTokenApplicationService;
import com.mt.access.application.role.RoleApplicationService;
import com.mt.access.application.sub_request.SubRequestApplicationService;
import com.mt.access.application.ticket.TicketApplicationService;
import com.mt.access.application.token.TokenApplicationService;
import com.mt.access.application.user.UserApplicationService;
import com.mt.access.application.user.UserRelationApplicationService;
import com.mt.access.infrastructure.operation_cool_down.JdbcCoolDownService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * application service registry.
 */
@Slf4j
@Component
public class ApplicationServiceRegistry {
    @Getter
    private static ClientApplicationService clientApplicationService;
    @Getter
    private static ProjectApplicationService projectApplicationService;
    @Getter
    private static RoleApplicationService roleApplicationService;
    @Getter
    private static PermissionApplicationService permissionApplicationService;
    @Getter
    private static VerificationCodeApplicationService verificationCodeApplicationService;
    @Getter
    private static UserApplicationService userApplicationService;
    @Getter
    private static EndpointApplicationService endpointApplicationService;
    @Getter
    private static CorsProfileApplicationService corsProfileApplicationService;
    @Getter
    private static RevokeTokenApplicationService revokeTokenApplicationService;
    @Getter
    private static TicketApplicationService ticketApplicationService;
    @Getter
    private static CacheProfileApplicationService cacheProfileApplicationService;
    @Getter
    private static ProxyApplicationService proxyApplicationService;
    @Getter
    private static UserRelationApplicationService userRelationApplicationService;
    @Getter
    private static JdbcCoolDownService emailDeliverApplicationService;
    @Getter
    private static NotificationApplicationService notificationApplicationService;
    @Getter
    private static RegistryApplicationService registryApplicationService;
    @Getter
    private static ImageApplicationService imageApplicationService;
    @Getter
    private static CrossDomainValidationApplicationService crossDomainValidationApplicationService;
    @Getter
    private static AuditApplicationService auditApplicationService;
    @Getter
    private static ReportApplicationService reportApplicationService;
    @Getter
    private static SubRequestApplicationService subRequestApplicationService;
    @Getter
    private static TokenApplicationService tokenApplicationService;

    @Autowired
    public void setTokenApplicationService(
        TokenApplicationService tokenApplicationService) {
        ApplicationServiceRegistry.tokenApplicationService =
            tokenApplicationService;
    }

    @Autowired
    public void setReportApplicationService(
        ReportApplicationService reportApplicationService) {
        ApplicationServiceRegistry.reportApplicationService =
            reportApplicationService;
    }

    @Autowired
    public void setCrossDomainValidationApplicationService(
        CrossDomainValidationApplicationService crossDomainValidationApplicationService) {
        ApplicationServiceRegistry.crossDomainValidationApplicationService =
            crossDomainValidationApplicationService;
    }

    @Autowired
    public void setImageApplicationService(
        ImageApplicationService imageApplicationService) {
        ApplicationServiceRegistry.imageApplicationService = imageApplicationService;
    }

    @Autowired
    public void setAuditApplicationService(
        AuditApplicationService auditApplicationService) {
        ApplicationServiceRegistry.auditApplicationService = auditApplicationService;
    }

    @Autowired
    public void setRegistryApplicationService(
        RegistryApplicationService registryApplicationService) {
        ApplicationServiceRegistry.registryApplicationService = registryApplicationService;
    }

    @Autowired
    public void setEmailDeliverApplicationService(
        JdbcCoolDownService emailDeliverApplicationService) {
        ApplicationServiceRegistry.emailDeliverApplicationService = emailDeliverApplicationService;
    }


    @Autowired
    public void setNotificationApplicationService(
        NotificationApplicationService notificationApplicationService) {
        ApplicationServiceRegistry.notificationApplicationService = notificationApplicationService;
    }

    @Autowired
    public void setUserRelationApplicationService(
        UserRelationApplicationService userRelationApplicationService) {
        ApplicationServiceRegistry.userRelationApplicationService = userRelationApplicationService;
    }

    @Autowired
    public void setProjectApplicationService(ProjectApplicationService projectApplicationService) {
        ApplicationServiceRegistry.projectApplicationService = projectApplicationService;
    }

    @Autowired
    public void setPermissionApplicationService(
        PermissionApplicationService permissionApplicationService) {
        ApplicationServiceRegistry.permissionApplicationService = permissionApplicationService;
    }

    @Autowired
    public void setRoleApplicationService(RoleApplicationService roleApplicationService) {
        ApplicationServiceRegistry.roleApplicationService = roleApplicationService;
    }

    @Autowired
    public void setProxyApplicationService(ProxyApplicationService proxyApplicationService) {
        ApplicationServiceRegistry.proxyApplicationService = proxyApplicationService;
    }

    @Autowired
    public void setCorsProfileApplicationService(
        CorsProfileApplicationService corsProfileApplicationService) {
        ApplicationServiceRegistry.corsProfileApplicationService = corsProfileApplicationService;
    }

    @Autowired
    public void setCacheProfileApplicationService(
        CacheProfileApplicationService cacheProfileApplicationService) {
        ApplicationServiceRegistry.cacheProfileApplicationService = cacheProfileApplicationService;
    }

    @Autowired
    public void setTicketApplicationService(TicketApplicationService ticketApplicationService) {
        ApplicationServiceRegistry.ticketApplicationService = ticketApplicationService;
    }

    @Autowired
    public void setRevokeTokenApplicationService(
        RevokeTokenApplicationService revokeTokenApplicationService) {
        ApplicationServiceRegistry.revokeTokenApplicationService = revokeTokenApplicationService;
    }

    @Autowired
    public void setEndpointApplicationService(
        EndpointApplicationService endpointApplicationService) {
        ApplicationServiceRegistry.endpointApplicationService = endpointApplicationService;
    }

    @Autowired
    public void setUserApplicationService(UserApplicationService userApplicationService) {
        ApplicationServiceRegistry.userApplicationService = userApplicationService;
    }

    @Autowired
    public void setSubRequestApplicationService(
        SubRequestApplicationService subRequestApplicationService) {
        ApplicationServiceRegistry.subRequestApplicationService = subRequestApplicationService;
    }

    /**
     * set the client application service.
     *
     * @param clientApplicationService client application service
     */
    @Autowired
    public void setClientApplicationService(ClientApplicationService clientApplicationService) {
        ApplicationServiceRegistry.clientApplicationService = clientApplicationService;
    }

    @Autowired
    public void setVerificationCodeApplicationService(
        VerificationCodeApplicationService verificationCodeApplicationService) {
        ApplicationServiceRegistry.verificationCodeApplicationService =
            verificationCodeApplicationService;
    }


}
