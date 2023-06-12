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
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.math.BigDecimal;
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

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"clientId", "path", "method"}))
@Slf4j
@NoArgsConstructor
@Getter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "endpointRegion")
public class Endpoint extends Auditable {

    @Column(name = "secured")
    private Boolean secured;

    @Setter(AccessLevel.PRIVATE)
    private String description;
    private String name;

    @Column(name = "websocket")
    private Boolean websocket;

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
            column = @Column(name = "clientId"))
    })
    private ClientId clientId;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "projectId"))
    })
    private ProjectId projectId;

    private String path;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private EndpointId endpointId;

    @Setter(AccessLevel.PRIVATE)
    private String method;

    @Setter(AccessLevel.PRIVATE)
    private Boolean csrfEnabled;

    @Column
    private Boolean shared;

    @Column
    private Boolean external;

    private Integer replenishRate = 0;

    private Integer burstCapacity = 0;

    private Boolean expired;
    private String expireReason;


    public Endpoint(ClientId clientId, ProjectId projectId, CacheProfileId cacheProfileId,
                    String name, String description,
                    String path, EndpointId endpointId, String method,
                    Boolean secured, Boolean websocket, Boolean csrfEnabled,
                    CorsProfileId corsProfileId, Boolean shared,
                    Boolean external, Integer replenishRate, Integer burstCapacity
    ) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setClientId(clientId);
        setProjectId(projectId);
        setEndpointId(endpointId);
        setName(name);
        setDescription(description);
        setWebsocket(websocket);
        setCacheProfileId(cacheProfileId);
        setPath(path);
        setMethod(method);
        setCsrfEnabled(csrfEnabled);
        setCorsProfileId(corsProfileId);
        setEndpointCatalogOnCreation(shared, secured, external);
        setExpired(Boolean.FALSE);
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        DomainRegistry.getEndpointValidationService()
            .validate(this, new HttpValidationNotificationHandler());
        CommonDomainRegistry.getDomainEventRepository().append(new EndpointCollectionModified());
        validate(new HttpValidationNotificationHandler());
    }

    private void setExpired(Boolean expired) {
        Validator.notNull(expired);
        this.expired = expired;
    }

    public void remove() {
        canBeRemoved();
        DomainRegistry.getEndpointRepository().remove(this);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new EndpointCollectionModified());
        if (secured) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new SecureEndpointRemoved(this));
        }
    }

    private void canBeRemoved() {
        if (shared && !expired) {
            throw new DefinedRuntimeException(
                "shared endpoint must be expired first before deletion", "1040",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void update(
        CacheProfileId cacheProfileId,
        String name, String description, String path, String method,
        Boolean isWebsocket,
        Boolean csrfEnabled, CorsProfileId corsProfileId,
        Integer replenishRate,
        Integer burstCapacity
    ) {
        if (expired) {
            throw new DefinedRuntimeException("expired endpoint cannot be updated", "1041",
                HttpResponseCode.BAD_REQUEST);
        }
        setName(name);
        setDescription(description);
        setWebsocket(isWebsocket);
        setCacheProfileId(cacheProfileId);
        setPath(path);
        setMethod(method);
        setCsrfEnabled(csrfEnabled);
        setCorsProfileId(corsProfileId);
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getEndpointValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setEndpointCatalogOnCreation(Boolean shared, Boolean secured, Boolean external) {
        if (Boolean.TRUE.equals(secured)) {
            permissionId = new PermissionId();
        }
        setShared(shared);
        setSecured(secured);
        setExternal(external);
        if (secured) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new SecureEndpointCreated(getProjectId(), this));
        }
    }

    private void setSecured(Boolean secured) {
        Validator.notNull(secured);
        Validator.isNull(this.secured);
        this.secured = secured;
    }

    private void setWebsocket(Boolean websocket) {
        Validator.notNull(websocket);
        Validator.isNull(this.websocket);
        this.websocket = websocket;
    }

    private void setShared(Boolean shared) {
        Validator.notNull(shared);
        Validator.isNull(this.shared);
        this.shared = shared;
    }

    private void setExternal(Boolean external) {
        Validator.notNull(external);
        Validator.isNull(this.external);
        this.external = external;
    }

    private void setName(String name) {
        Validator.notBlank(name);
        this.name = name;
    }

    private void setReplenishRate(Integer replenishRate) {
        Validator.notNull(replenishRate);
        Validator.greaterThan(new BigDecimal(replenishRate), BigDecimal.ZERO);
        this.replenishRate = replenishRate;
    }

    private void setBurstCapacity(Integer burstCapacity) {
        Validator.notNull(burstCapacity);
        Validator.greaterThan(new BigDecimal(replenishRate), BigDecimal.ZERO);
        this.burstCapacity = burstCapacity;
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
            throw new DefinedRuntimeException("endpoint can only expire once", "1042",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!this.external) {
            throw new DefinedRuntimeException("internal endpoint cannot be expired", "1043",
                HttpResponseCode.BAD_REQUEST);
        }
        if (this.shared) {
            this.expired = true;
            Validator.notBlank(expireReason);
            this.expireReason = expireReason;
            CommonDomainRegistry.getDomainEventRepository().append(new EndpointExpired(this));
        } else {
            throw new DefinedRuntimeException("only shared endpoint can be expired", "1044",
                HttpResponseCode.BAD_REQUEST);
        }
    }
}
