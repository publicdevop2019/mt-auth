package com.mt.access.domain;

import com.mt.access.domain.model.*;
import com.mt.access.domain.model.activation_code.ActivationCodeService;
import com.mt.access.domain.model.cache_profile.CacheProfileRepository;
import com.mt.access.domain.model.client.ClientRepository;
import com.mt.access.domain.model.client.ClientValidationService;
import com.mt.access.domain.model.cors_profile.CORSProfileRepository;
import com.mt.access.domain.model.endpoint.EndpointRepository;
import com.mt.access.domain.model.endpoint.EndpointService;
import com.mt.access.domain.model.pending_user.PendingUserRepository;
import com.mt.access.domain.model.pending_user.PendingUserService;
import com.mt.access.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.access.domain.model.revoke_token.RevokeTokenService;
import com.mt.access.domain.model.system_role.SystemRoleRepository;
import com.mt.access.domain.model.ticket.TicketService;
import com.mt.access.domain.model.user.PasswordResetTokenService;
import com.mt.access.domain.model.user.UserRepository;
import com.mt.access.domain.model.user.UserService;
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
    private static AuthenticationService authenticationService;
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
    private static SystemRoleRepository systemRoleRepository;
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
    private static CORSProfileRepository corsProfileRepository;
    @Getter
    private static CacheProfileRepository cacheProfileRepository;
    @Getter
    private static ProxyService proxyService;

    @Autowired
    public void setProxyService(ProxyService proxyService) {
        DomainRegistry.proxyService = proxyService;
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
    public void setSystemRoleRepository(SystemRoleRepository systemRoleRepository) {
        DomainRegistry.systemRoleRepository = systemRoleRepository;
    }

    @Autowired
    public void setCORSProfileRepository(CORSProfileRepository corsProfileRepository) {
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
    public void setPendingUserValidationService(PendingUserValidationService pendingUserValidationService) {
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
    public void setAuthenticationService(AuthenticationService authenticationService) {
        DomainRegistry.authenticationService = authenticationService;
    }

    @Autowired
    public void setPendingUserService(PendingUserService pendingUserService) {
        DomainRegistry.pendingUserService = pendingUserService;
    }

}
