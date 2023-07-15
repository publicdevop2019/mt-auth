package com.mt.access.domain.model.endpoint;

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
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.http.HttpMethod;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"clientId", "path", "method"}))
@Slf4j
@NoArgsConstructor
@Getter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "endpointRegion")
public class Endpoint extends Auditable {
    private static final Set<String> HTTP_METHODS =
        Arrays.stream(HttpMethod.values()).map(Enum::name).collect(
            Collectors.toSet());
    private static final Pattern PATH_REGEX =
        Pattern.compile("^[a-z\\-/*]*$");
    @Column(name = "secured")
    private Boolean secured;

    private String description;
    private String name;

    @Column(name = "websocket")
    private Boolean websocket;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "cors_profile_id"))
    })
    private CorsProfileId corsProfileId;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "permission_id"))
    })
    private PermissionId permissionId;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
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

    private String method;

    private Boolean csrfEnabled;

    @Column
    private Boolean shared;

    @Column
    private Boolean external;

    private Integer replenishRate;

    private Integer burstCapacity;

    private Boolean expired;
    private String expireReason;


    public Endpoint(ClientId clientId, ProjectId projectId, CacheProfileId cacheProfileId,
                    String name, String description,
                    String path, EndpointId endpointId, String method,
                    Boolean secured, Boolean websocket, Boolean csrfEnabled,
                    CorsProfileId corsProfileId, Boolean shared,
                    Boolean external, Integer replenishRate, Integer burstCapacity, TransactionContext context
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
        setEndpointCatalogOnCreation(shared, secured, external,context);
        initExpired();
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        context.append(new EndpointCollectionModified());
        validate(new HttpValidationNotificationHandler());
    }

    public static Endpoint addNewEndpoint(
        ClientId clientId, ProjectId projectId,
        CacheProfileId cacheProfileId, String name, String description, String path,
        EndpointId endpointId, String method, Boolean secured,
        Boolean websocket, Boolean csrfEnabled, CorsProfileId corsProfileId, Boolean shared,
        Boolean external, Integer replenishRate,
        Integer burstCapacity, TransactionContext context
    ) {
        return new Endpoint(clientId, projectId, cacheProfileId, name, description, path,
            endpointId, method, secured, websocket, csrfEnabled, corsProfileId, shared, external,
            replenishRate, burstCapacity,context);
    }

    private void setCsrfEnabled(Boolean csrfEnabled) {
        this.csrfEnabled = csrfEnabled;
    }

    private void setMethod(String method) {
        if (Checker.notNull(method)) {
            method = method.toUpperCase();
            Validator.memberOf(method, HTTP_METHODS);
        }
        this.method = method;
    }

    private void initExpired() {
        this.expired = Boolean.FALSE;
    }

    public void remove(TransactionContext context) {
        canBeRemoved();
        DomainRegistry.getEndpointRepository().remove(this);
        context
            .append(new EndpointCollectionModified());
        if (secured) {
            context
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
        setCacheProfileId(cacheProfileId);
        setPath(path);
        setMethod(method);
        setCsrfEnabled(csrfEnabled);
        setCorsProfileId(corsProfileId);
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        validate(new HttpValidationNotificationHandler());
    }

    private void setDescription(String description) {
        Validator.validOptionalString(100, description);
        this.description = description;
    }

    private void setEndpointCatalogOnCreation(Boolean shared, Boolean secured, Boolean external, TransactionContext context) {
        if (Boolean.TRUE.equals(secured)) {
            permissionId = new PermissionId();
        }
        setShared(shared);
        setSecured(secured);
        setExternal(external);
        if (secured) {
            context
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
        Validator.validRequiredString(1, 50, name);
        this.name = name;
    }

    private void setReplenishRate(Integer replenishRate) {
        if (Checker.notNull(replenishRate)) {
            Validator.greaterThanOrEqualTo(replenishRate, 1);
            Validator.lessThanOrEqualTo(replenishRate, 1000);
        }
        this.replenishRate = replenishRate;
    }

    private void setBurstCapacity(Integer burstCapacity) {
        if (Checker.notNull(burstCapacity)) {
            Validator.greaterThanOrEqualTo(burstCapacity, 1);
            Validator.lessThanOrEqualTo(burstCapacity, 1500);
        }
        this.burstCapacity = burstCapacity;
    }

    private void setPath(String path) {
        Validator.notNull(path);
        Validator.lessThanOrEqualTo(path, 100);
        Validator.greaterThanOrEqualTo(path, 5);
        Matcher matcher = PATH_REGEX.matcher(path);//alpha - / * only
        boolean result = false;
        if (matcher.find()) {
            if (!path.startsWith("/") && !path.endsWith("/")) { //avoid /test/
                if (!path.endsWith("-") && !path.startsWith("-")) { //avoid -test-
                    if (!path.startsWith("*")) { //avoid *test
                        if (path.contains("/")) {
                            boolean valid = true;
                            for (String s : path.split("/")) {
                                if (s.isBlank()) {
                                    valid = false;
                                    break;
                                }
                                if (s.startsWith("-") || s.endsWith("-")) {
                                    valid = false;
                                    break;
                                }
                                if (s.contains("*") && !s.equalsIgnoreCase("**")) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (valid) {
                                result = true;
                            }
                        } else {
                            if (!path.contains("*")) {
                                result = true;
                            }
                        }
                    }
                }
            }
        }
        if (!result) {
            throw new DefinedRuntimeException("invalid endpoint path format", "1086",
                HttpResponseCode.BAD_REQUEST);
        }
        this.path = path;
    }

    @Override
    public void validate(ValidationNotificationHandler handler) {
        DomainRegistry.getEndpointValidationService()
            .validate(this, new HttpValidationNotificationHandler());
        (new EndpointValidator(this, handler)).validate();
    }

    public void expire(String expireReason, TransactionContext context) {
        Validator.validRequiredString(1, 50, expireReason);
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
            this.expireReason = expireReason;
            context.append(new EndpointExpired(this));
        } else {
            throw new DefinedRuntimeException("only shared endpoint can be expired", "1044",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void removeCorsRef() {
        this.corsProfileId = null;
    }

    public void removeCacheProfileRef() {
        this.cacheProfileId = null;
    }
}
