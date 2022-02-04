package com.mt.access.application.client;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.command.ClientCreateCommand;
import com.mt.access.application.client.command.ClientPatchCommand;
import com.mt.access.application.client.command.ClientUpdateCommand;
import com.mt.access.application.client.representation.ClientSpringOAuth2Representation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.*;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientApplicationService implements ClientDetailsService {

    public static final String CLIENT = "Client";

    @SubscribeForEvent
    @Transactional
    public String create(ClientCreateCommand command, String operationId) {
        ClientId clientId = new ClientId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(operationId,
                (change) -> {
                    Client client = new Client(
                            clientId,
                            new ProjectId(command.getProjectId()),
                            command.getName(),
                            command.getPath(),
                            command.getClientSecret(),
                            command.getDescription(),
                            command.isResourceIndicator(),
                            command.getResourceIds() != null ? command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.emptySet(),
                            command.getGrantTypeEnums(),
                            new TokenDetail(command.getAccessTokenValiditySeconds(), command.getRefreshTokenValiditySeconds()),
                            new RedirectDetail(
                                    command.getRegisteredRedirectUri(),
                                    command.isAutoApprove()
                            ),
                            command.getTypes()
                    );
                    return client.getClientId().getDomainId();
                }, CLIENT
        );

    }

    public SumPagedRep<Client> clients(String queryParam, String pagingParam, String configParam) {
        return DomainRegistry.getClientRepository().clientsOfQuery(new ClientQuery(queryParam, pagingParam, configParam, false));
    }
    public SumPagedRep<Client> internalClients(String pagingParam, String configParam) {
        return DomainRegistry.getClientRepository().clientsOfQuery(new ClientQuery(null, pagingParam, configParam, true));
    }

    public Optional<Client> client(String id) {
        return DomainRegistry.getClientRepository().clientOfId(new ClientId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replaceClient(String id, ClientUpdateCommand command, String changeId) {
        ClientId clientId = new ClientId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Client> optionalClient = DomainRegistry.getClientRepository().clientOfId(clientId);
            if (optionalClient.isPresent()) {
                Client client = optionalClient.get();
                client.replace(
                        command.getName(),
                        command.getClientSecret(),
                        command.getPath(),
                        command.getDescription(),
                        command.isResourceIndicator(),
                        command.getResourceIds() != null ? command.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.emptySet(),
                        command.getGrantTypeEnums(),
                        new TokenDetail(command.getAccessTokenValiditySeconds(), command.getRefreshTokenValiditySeconds()),
                        new RedirectDetail(
                                command.getRegisteredRedirectUri(),
                                command.isAutoApprove()
                        )
                );
                DomainRegistry.getClientRepository().add(client);
            }
            return null;
        }, CLIENT);
    }

    @SubscribeForEvent
    @Transactional
    public void removeClient(String id, String changeId) {
        ClientId clientId = new ClientId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Client> client = DomainRegistry.getClientRepository().clientOfId(clientId);
            if (client.isPresent()) {
                Client client1 = client.get();
                if (client1.removable()) {
                    DomainRegistry.getClientRepository().remove(client1);
                    client1.removeAllReferenced();
                } else {
                    throw new RootClientDeleteException();
                }
            }
            return null;
        }, CLIENT);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ClientId clientId = new ClientId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Client> client = DomainRegistry.getClientRepository().clientOfId(clientId);
            if (client.isPresent()) {
                Client original = client.get();
                ClientPatchCommand beforePatch = new ClientPatchCommand(original);
                ClientPatchCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, ClientPatchCommand.class);
                original.replace(
                        afterPatch.getName(),
                        null,
                        afterPatch.getPath(),
                        afterPatch.getDescription(),
                        afterPatch.isResourceIndicator(),
                        afterPatch.getResourceIds() != null ? afterPatch.getResourceIds().stream().map(ClientId::new).collect(Collectors.toSet()) : Collections.emptySet(),
                        afterPatch.getGrantTypeEnums(),
                        new TokenDetail(afterPatch.getAccessTokenValiditySeconds(), original.getTokenDetail().getRefreshTokenValiditySeconds()),
                        original.getAuthorizationCodeGrant()
                );
            }
            return null;
        }, CLIENT);
    }

    @Override
    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        Optional<Client> client = DomainRegistry.getClientRepository().clientOfId(new ClientId(id));
        return client.map(ClientSpringOAuth2Representation::new).orElse(null);
    }

    @SubscribeForEvent
    @Transactional
    public void handleChange(ClientAsResourceDeleted deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            //remove deleted client from resource_map
            DomainId domainId = deserialize.getDomainId();
            ClientId removedClientId = new ClientId(domainId.getDomainId());
            Set<Client> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) query), ClientQuery.resourceIds(removedClientId));
            allByQuery.forEach(e -> e.removeResource(removedClientId));
            Set<ClientId> collect = allByQuery.stream().map(Client::getClientId).collect(Collectors.toSet());
            collect.add(removedClientId);
            DomainEventPublisher.instance().publish(new ClientResourceCleanUpCompleted(collect));
            return null;
        }, CLIENT);
    }

    public static class RootClientDeleteException extends RuntimeException {
    }
}
