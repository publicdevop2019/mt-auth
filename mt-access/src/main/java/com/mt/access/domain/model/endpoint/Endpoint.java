package com.mt.access.domain.model.endpoint;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.endpoint.event.EndpointCollectionModified;
import com.mt.access.domain.model.endpoint.event.EndpointExpired;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"clientId", "path", "method",
    "deleted"}))
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "endpointRegion")
public class Endpoint extends Auditable {

    @Column(name = "secured", updatable = false)
    private boolean authRequired;

    @Setter(AccessLevel.PRIVATE)
    private String description;
    private String name;

    @Setter(AccessLevel.PRIVATE)
    private boolean isWebsocket;

    @Embedded
    @Setter(AccessLevel.PUBLIC)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "cors_profile_id"))
    })
    private CorsProfileId corsProfileId;

    @Embedded
    @Setter(AccessLevel.PUBLIC)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "permission_id"))
    })
    private PermissionId permissionId;
    @Embedded
    @Setter(AccessLevel.PUBLIC)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "cache_profile_id"))
    })
    private CacheProfileId cacheProfileId;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "clientId", updatable = false, nullable = false))
    })
    private ClientId clientId;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "projectId", updatable = false, nullable = false))
    })
    private ProjectId projectId;

    private String path;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private EndpointId endpointId;

    @Setter(AccessLevel.PRIVATE)
    private String method;

    @Setter(AccessLevel.PRIVATE)
    private boolean csrfEnabled = true;

    @Column(updatable = false)
    private boolean shared = false;

    @Column(updatable = false)
    private boolean external = false;

    private int replenishRate = 0;

    private int burstCapacity = 0;

    private boolean expired = false;
    private String expireReason;


    public Endpoint(ClientId clientId, ProjectId projectId, CacheProfileId cacheProfileId,
                    String name, String description,
                    String path, EndpointId endpointId, String method,
                    boolean authRequired, boolean isWebsocket, boolean csrfEnabled,
                    CorsProfileId corsProfileId, boolean shared,
                    boolean external, int replenishRate, int burstCapacity
    ) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setClientId(clientId);
        setProjectId(projectId);
        setEndpointId(endpointId);
        setName(name);
        setDescription(description);
        setWebsocket(isWebsocket);
        setCacheProfileId(cacheProfileId);
        setPath(path);
        setMethod(method);
        setCsrfEnabled(csrfEnabled);
        setCorsProfileId(corsProfileId);
        validate(new HttpValidationNotificationHandler());
        setEndpointCatalogOnCreation(shared, authRequired, external);
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        DomainRegistry.getEndpointValidationService()
            .validate(this, new HttpValidationNotificationHandler());
        CommonDomainRegistry.getDomainEventRepository().append(new EndpointCollectionModified());
    }

    public static void remove(Set<Endpoint> endpointSet) {
        endpointSet.forEach(Endpoint::canBeRemoved);
        DomainRegistry.getEndpointRepository().remove(endpointSet);
        CommonDomainRegistry.getDomainEventRepository().append(
            new EndpointCollectionModified()
        );
        Set<Endpoint> collect =
            endpointSet.stream().filter(Endpoint::isAuthRequired).collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new SecureEndpointRemoved(collect));
        }
    }

    public void remove() {
        canBeRemoved();
        DomainRegistry.getEndpointRepository().remove(this);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new EndpointCollectionModified());
        if (authRequired) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new SecureEndpointRemoved(Collections.singleton(this)));
        }
    }

    private void canBeRemoved() {
        if (shared && !expired) {
            throw new IllegalStateException(
                "shared endpoint must be expired first before deletion");
        }
    }

    public void update(
        CacheProfileId cacheProfileId,
        String name, String description, String path, String method,
        boolean isWebsocket,
        boolean csrfEnabled, CorsProfileId corsProfileId,
        int replenishRate,
        int burstCapacity
    ) {
        if (expired) {
            throw new IllegalStateException("expired endpoint cannot be updated");
        }
        setName(name);
        setDescription(description);
        setWebsocket(isWebsocket);
        setCacheProfileId(cacheProfileId);
        setPath(path);
        setMethod(method);
        setCsrfEnabled(csrfEnabled);
        setCorsProfileId(corsProfileId);
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getEndpointValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setEndpointCatalogOnCreation(boolean shared, boolean secured, boolean external) {
        if (secured) {
            permissionId = new PermissionId();
        }
        this.shared = shared;
        this.authRequired = secured;
        this.external = external;
        if (secured) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new SecureEndpointCreated(getProjectId(), this));
        }
    }

    private void setName(String name) {
        Validator.notBlank(name);
        this.name = name;
    }

    private void setPath(String path) {
        Validator.notBlank(path);
        this.path = path;
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new EndpointValidator(this, handler)).validate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Endpoint)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Endpoint endpoint = (Endpoint) o;
        return Objects.equal(endpointId, endpoint.endpointId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), endpointId);
    }

    public void expire(String expireReason) {
        if (this.expired) {
            throw new IllegalStateException("endpoint can only expire once");
        }
        if (this.shared) {
            this.expired = true;
            Validator.notBlank(expireReason);
            this.expireReason = expireReason;
            CommonDomainRegistry.getDomainEventRepository().append(new EndpointExpired(this));
        } else {
            throw new IllegalStateException("only shared endpoint can be expired");
        }
    }
}
