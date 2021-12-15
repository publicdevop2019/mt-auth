package com.mt.access.application;

import com.mt.access.application.cache_profile.CacheProfileApplicationService;
import com.mt.access.application.cors_profile.CORSProfileApplicationService;
import com.mt.access.application.proxy.ProxyApplicationService;
import com.mt.access.application.system_role.SystemRoleApplicationService;
import com.mt.access.infrastructure.RedisAuthorizationCodeServices;
import com.mt.common.domain.model.idempotent.IdempotentService;
import com.mt.access.application.client.ClientApplicationService;
import com.mt.access.application.endpoint.EndpointApplicationService;
import com.mt.access.application.pending_user.PendingUserApplicationService;
import com.mt.access.application.revoke_token.RevokeTokenApplicationService;
import com.mt.access.application.ticket.TicketApplicationService;
import com.mt.access.application.user.UserApplicationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationServiceRegistry {
    @Getter
    private static ClientApplicationService clientApplicationService;
    @Getter
    private static PendingUserApplicationService pendingUserApplicationService;
    @Getter
    private static UserApplicationService userApplicationService;
    @Getter
    private static EndpointApplicationService endpointApplicationService;
    @Getter
    private static CORSProfileApplicationService corsProfileApplicationService;
    @Getter
    private static RevokeTokenApplicationService revokeTokenApplicationService;
    @Getter
    private static TicketApplicationService ticketApplicationService;
    @Getter
    private static SystemRoleApplicationService systemRoleApplicationService;
    @Getter
    private static AuthorizeCodeApplicationService authorizeCodeApplicationService;
    @Getter
    private static IdempotentService applicationServiceIdempotentWrapper;
    @Getter
    private static CacheProfileApplicationService cacheProfileApplicationService;
    @Getter
    private static ProxyApplicationService proxyApplicationService;
    @Getter
    private static RedisAuthorizationCodeServices redisAuthorizationCodeServices;

    @Autowired
    public void setRedisAuthorizationCodeServices(RedisAuthorizationCodeServices redisAuthorizationCodeServices) {
        ApplicationServiceRegistry.redisAuthorizationCodeServices = redisAuthorizationCodeServices;
    }
    @Autowired
    public void setProxyApplicationService(ProxyApplicationService proxyApplicationService) {
        ApplicationServiceRegistry.proxyApplicationService = proxyApplicationService;
    }
    @Autowired
    public void setCORSProfileApplicationService(CORSProfileApplicationService corsProfileApplicationService) {
        ApplicationServiceRegistry.corsProfileApplicationService = corsProfileApplicationService;
    }
    @Autowired
    public void setCacheProfileApplicationService(CacheProfileApplicationService cacheProfileApplicationService) {
        ApplicationServiceRegistry.cacheProfileApplicationService = cacheProfileApplicationService;
    }
    @Autowired
    public void setSystemRoleApplicationService(SystemRoleApplicationService systemRoleApplicationService) {
        ApplicationServiceRegistry.systemRoleApplicationService = systemRoleApplicationService;
    }
    @Autowired
    public void setTicketApplicationService(TicketApplicationService ticketApplicationService) {
        ApplicationServiceRegistry.ticketApplicationService = ticketApplicationService;
    }

    @Autowired
    public void setRevokeTokenApplicationService(RevokeTokenApplicationService revokeTokenApplicationService) {
        ApplicationServiceRegistry.revokeTokenApplicationService = revokeTokenApplicationService;
    }

    @Autowired
    public void setEndpointApplicationService(EndpointApplicationService endpointApplicationService) {
        ApplicationServiceRegistry.endpointApplicationService = endpointApplicationService;
    }

    @Autowired
    public void setUserApplicationService(UserApplicationService userApplicationService) {
        ApplicationServiceRegistry.userApplicationService = userApplicationService;
    }

    @Autowired
    public void setClientApplicationService(ClientApplicationService clientApplicationService) {
        log.debug("[order1] setting clientApplicationService,value is {}", clientApplicationService == null ? "null" : "not null");
        ApplicationServiceRegistry.clientApplicationService = clientApplicationService;
    }

    @Autowired
    public void setAuthorizeCodeApplicationService(AuthorizeCodeApplicationService authorizeCodeApplicationService) {
        ApplicationServiceRegistry.authorizeCodeApplicationService = authorizeCodeApplicationService;
    }

    @Autowired
    public void setPendingUserApplicationService(PendingUserApplicationService pendingUserApplicationService) {
        ApplicationServiceRegistry.pendingUserApplicationService = pendingUserApplicationService;
    }

    @Autowired
    public void setClientIdempotentApplicationService(IdempotentService clientIdempotentApplicationService) {
        ApplicationServiceRegistry.applicationServiceIdempotentWrapper = clientIdempotentApplicationService;
    }



}
