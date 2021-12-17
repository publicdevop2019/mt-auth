package com.mt.access.application.endpoint;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.endpoint.command.EndpointCreateCommand;
import com.mt.access.application.endpoint.command.EndpointPatchCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.application.endpoint.representation.EndpointProxyCardRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.cors_profile.event.CORSProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CORSProfileUpdated;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.event.EndpointCollectionModified;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.AppStarted;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EndpointApplicationService {
    private static final String ENDPOINT = "Endpoint";
    @Value("${proxy.reload}")
    private boolean reloadOnAppStart;
    @Value("${spring.application.name}")
    private String appName;

    /**
     * send app started event with a delay of 60s to wait for registry complete
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void reloadProxy() {
        if (reloadOnAppStart) {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                log.error("wait is interrupted due to", e);
            }
            log.debug("sending reload proxy endpoint message");
            CommonDomainRegistry.getEventStreamService().next(appName, false, "started_access", new AppStarted());
        }
    }

    @SubscribeForEvent
    @Transactional
    public String create(EndpointCreateCommand command, String changeId) {
        EndpointId endpointId = new EndpointId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            String resourceId = command.getResourceId();
            Optional<Client> client = DomainRegistry.getClientRepository().clientOfId(new ClientId(resourceId));
            if (client.isPresent()) {
                Client client1 = client.get();
                Endpoint endpoint = client1.addNewEndpoint(
                        command.getRoleId() != null ? new SystemRoleId(command.getRoleId()) : null,
                        command.getCacheProfileId() != null ? new CacheProfileId(command.getCacheProfileId()) : null,
                        command.getDescription(),
                        command.getPath(),
                        endpointId,
                        command.getMethod(),
                        command.isSecured(),
                        command.isWebsocket(),
                        command.isCsrfEnabled(),
                        command.getCorsProfileId() != null ? new CORSProfileId(command.getCorsProfileId()) : null
                );
                DomainRegistry.getEndpointRepository().add(endpoint);
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
                return endpointId.getDomainId();
            } else {
                throw new InvalidClientIdException();
            }
        }, ENDPOINT);
    }

    public SumPagedRep<Endpoint> endpoints(String queryParam, String pageParam, String config) {
        return DomainRegistry.getEndpointRepository().endpointsOfQuery(new EndpointQuery(queryParam, pageParam, config));
    }

    public Optional<Endpoint> endpoint(String id) {
        return DomainRegistry.getEndpointRepository().endpointOfId(new EndpointId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void update(String id, EndpointUpdateCommand command, String changeId) {
        log.debug("start of update endpoint");
        EndpointId endpointId = new EndpointId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Endpoint> endpoint = DomainRegistry.getEndpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                endpoint1.update(
                        command.getRoleId() != null ? new SystemRoleId(command.getRoleId()) : null,
                        command.getCacheProfileId() != null ? new CacheProfileId(command.getCacheProfileId()) : null,
                        command.getDescription(),
                        command.getPath(),
                        command.getMethod(),
                        command.isSecured(),
                        command.isWebsocket(),
                        command.isCsrfEnabled(),
                        command.getCorsProfileId() != null ? new CORSProfileId(command.getCorsProfileId()) : null
                );
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
                DomainRegistry.getEndpointRepository().add(endpoint1);
            }
            return null;
        }, ENDPOINT);
        log.debug("end of update endpoint");
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoint(String id, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Endpoint> endpoint = DomainRegistry.getEndpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                DomainRegistry.getEndpointRepository().remove(endpoint1);
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            }
            return null;
        }, ENDPOINT);
    }

    @SubscribeForEvent
    @Transactional
    public void removeEndpoints(String queryParam, String changeId) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(queryParam));
            DomainRegistry.getEndpointRepository().remove(allByQuery);
            DomainEventPublisher.instance().publish(
                    new EndpointCollectionModified()
            );
            return null;
        }, ENDPOINT);
    }

    @SubscribeForEvent
    @Transactional
    public void patchEndpoint(String id, JsonPatch command, String changeId) {
        EndpointId endpointId = new EndpointId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Endpoint> endpoint = DomainRegistry.getEndpointRepository().endpointOfId(endpointId);
            if (endpoint.isPresent()) {
                Endpoint endpoint1 = endpoint.get();
                EndpointPatchCommand beforePatch = new EndpointPatchCommand(endpoint1);
                EndpointPatchCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, EndpointPatchCommand.class);
                endpoint1.update(
                        endpoint1.getSystemRoleId(),
                        endpoint1.getCacheProfileId(),
                        afterPatch.getDescription(),
                        afterPatch.getPath(),
                        afterPatch.getMethod(),
                        endpoint1.isSecured(),
                        endpoint1.isWebsocket(),
                        endpoint1.isCsrfEnabled(),
                        endpoint1.getCorsProfileId()
                );
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            }
            return null;
        }, ENDPOINT);
    }

    @SubscribeForEvent
    @Transactional
    public void reloadEndpointCache(String changeId) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            DomainRegistry.getEndpointService().reloadEndpointCache();
            return null;
        }, ENDPOINT);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(ClientDeleted deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle delete client event");
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(new ClientId(deserialize.getDomainId().getDomainId())));
            if (!allByQuery.isEmpty()) {
                DomainRegistry.getEndpointRepository().remove(allByQuery);
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            }
            return null;
        }, ENDPOINT);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(CORSProfileRemoved deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle cors profile removed");
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(new CORSProfileId(deserialize.getDomainId().getDomainId())));
            if (!allByQuery.isEmpty()) {
                allByQuery.forEach(e -> e.setCorsProfileId(null));
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            }
            return null;
        }, ENDPOINT);
    }

    /**
     * refresh proxy when referred cors profile is updated
     *
     * @param deserialize
     */
    @SubscribeForEvent
    @Transactional
    public void handle(CORSProfileUpdated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle cors profile updated");
            CORSProfileId corsProfileId = new CORSProfileId(deserialize.getDomainId().getDomainId());
            SumPagedRep<Endpoint> endpointSumPagedRep = DomainRegistry.getEndpointRepository().endpointsOfQuery(new EndpointQuery(corsProfileId));
            if (endpointSumPagedRep.findFirst().isPresent()) {
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            }
            return null;
        }, ENDPOINT);
    }

    /**
     * refresh proxy when referred role group is updated
     *
     * @param deserialize
     */
    @SubscribeForEvent
    @Transactional
    public void handle(SystemRoleDeleted deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle role group removed");
            SystemRoleId systemRoleId = new SystemRoleId(deserialize.getDomainId().getDomainId());
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(systemRoleId));
            if (!allByQuery.isEmpty()) {
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
                allByQuery.forEach(e -> e.setSystemRoleId(null));
            }
            return null;
        }, ENDPOINT);

    }

    @SubscribeForEvent
    @Transactional
    public void handle(CacheProfileRemoved deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle cache profile removed");
            CacheProfileId profileId = new CacheProfileId(deserialize.getDomainId().getDomainId());
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery(profileId));
            if (!allByQuery.isEmpty()) {
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
                allByQuery.forEach(e -> e.setCacheProfileId(null));
            }
            return null;
        }, ENDPOINT);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(CacheProfileUpdated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle cache profile updated");
            CacheProfileId profileId = new CacheProfileId(deserialize.getDomainId().getDomainId());
            SumPagedRep<Endpoint> firstPage = DomainRegistry.getEndpointRepository().endpointsOfQuery(new EndpointQuery(profileId));
            if (firstPage.findFirst().isPresent()) {
                DomainEventPublisher.instance().publish(new EndpointCollectionModified());
            } else {
                log.debug("cache profile is not used");
            }
            return null;
        }, ENDPOINT);
    }

    public SumPagedRep<EndpointProxyCardRepresentation> proxyEndpoints(String queryParam, String pageParam, String config) {
        SumPagedRep<Endpoint> endpoints = endpoints(queryParam, pageParam, config);
        List<EndpointProxyCardRepresentation> collect = endpoints.getData().stream().map(EndpointProxyCardRepresentation::new).collect(Collectors.toList());
        EndpointProxyCardRepresentation.updateDetail(collect);
        return new SumPagedRep<>(collect, endpoints.getTotalItemCount());
    }

    public static class InvalidClientIdException extends RuntimeException {
    }
}
