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
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@NoArgsConstructor
@Getter
public class Endpoint extends Auditable {
    private static final Set<String> HTTP_METHODS =
        Arrays.stream(HttpMethod.values()).map(Enum::name).collect(
            Collectors.toSet());
    private static final Pattern ALLOWED_CHARS =
        Pattern.compile("^[a-zA-Z\\-./*]*$");

    static {
        HTTP_METHODS.add("WEB_SOCKET");
    }

    private Boolean secured;

    private String description;
    private String name;

    private Boolean websocket;

    @Setter(AccessLevel.PRIVATE)
    private CorsProfileId corsProfileId;

    @Setter(AccessLevel.PRIVATE)
    private PermissionId permissionId;
    @Setter(AccessLevel.PRIVATE)
    private CacheProfileId cacheProfileId;

    @Setter(AccessLevel.PRIVATE)
    private ClientId clientId;
    @Setter(AccessLevel.PRIVATE)
    private ProjectId projectId;

    private String path;

    @Setter(AccessLevel.PRIVATE)
    private EndpointId endpointId;

    private String method;

    private Boolean csrfEnabled;

    private Boolean shared;

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
                    Boolean external, Integer replenishRate, Integer burstCapacity,
                    TransactionContext context
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
        setEndpointCatalogOnCreation(shared, secured, external, context);
        initExpired();
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        context.append(new EndpointCollectionModified());
        validate(new HttpValidationNotificationHandler());
        long milli = Instant.now().toEpochMilli();
        setCreatedAt(milli);
        setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        setModifiedAt(milli);
        setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
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
            replenishRate, burstCapacity, context);
    }

    public static Endpoint fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                           Long modifiedAt, String modifiedBy, Integer version,
                                           CacheProfileId cacheProfileId, ClientId clientId,
                                           CorsProfileId corsProfileId, Boolean csrfEnabled,
                                           String description, EndpointId domainId,
                                           Boolean websocket, String method, String name,
                                           String path, PermissionId permissionId,
                                           ProjectId projectId,
                                           Boolean secured, Boolean shared, String expireReason,
                                           Boolean expired, Boolean external, Integer replenishRate,
                                           Integer burstCapacity) {
        Endpoint endpoint = new Endpoint();
        endpoint.setId(id);
        endpoint.setCreatedAt(createdAt);
        endpoint.setCreatedBy(createdBy);
        endpoint.setModifiedAt(modifiedAt);
        endpoint.setModifiedBy(modifiedBy);
        endpoint.setVersion(version);
        endpoint.setCacheProfileId(cacheProfileId);
        endpoint.setClientId(clientId);
        endpoint.setCorsProfileId(corsProfileId);
        endpoint.setCsrfEnabled(csrfEnabled);
        endpoint.setDescription(description);
        endpoint.setEndpointId(domainId);
        endpoint.setWebsocket(websocket);
        endpoint.setMethod(method);
        endpoint.setName(name);
        endpoint.path = path;
        endpoint.setPermissionId(permissionId);
        endpoint.setProjectId(projectId);
        endpoint.setSecured(secured);
        endpoint.setShared(shared);
        endpoint.expireReason = expireReason;
        endpoint.expired = expired;
        endpoint.setExternal(external);
        endpoint.setReplenishRate(replenishRate);
        endpoint.setBurstCapacity(burstCapacity);
        return endpoint;
    }

    private void setCsrfEnabled(Boolean csrfEnabled) {
        this.csrfEnabled = csrfEnabled;
    }

    private void setMethod(String method) {
        Validator.notNull(method);
        method = method.toUpperCase();
        Validator.memberOf(method, HTTP_METHODS);
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

    public void removeAfterClientDelete(TransactionContext context) {
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

    public Endpoint update(
        CacheProfileId cacheProfileId,
        String name, String description, String path, String method,
        Boolean csrfEnabled, CorsProfileId corsProfileId,
        Integer replenishRate,
        Integer burstCapacity
    ) {
        Endpoint updated = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        if (updated.expired) {
            throw new DefinedRuntimeException("expired endpoint cannot be updated", "1041",
                HttpResponseCode.BAD_REQUEST);
        }
        updated.setName(name);
        updated.setDescription(description);
        updated.setCacheProfileId(cacheProfileId);
        updated.setPath(path);
        updated.setMethod(method);
        updated.setCsrfEnabled(csrfEnabled);
        updated.setCorsProfileId(corsProfileId);
        updated.setReplenishRate(replenishRate);
        updated.setBurstCapacity(burstCapacity);
        updated.validate(new HttpValidationNotificationHandler());
        updated.setModifiedAt(Instant.now().toEpochMilli());
        updated.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        return updated;
    }

    private void setDescription(String description) {
        Validator.validOptionalString(100, description);
        this.description = description;
    }

    private void setEndpointCatalogOnCreation(Boolean shared, Boolean secured, Boolean external,
                                              TransactionContext context) {
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
        Matcher matcher = ALLOWED_CHARS.matcher(path);//alpha - / * only
        boolean result = false;
        if (matcher.find()) {
            if (
                !path.startsWith("/") && !path.endsWith("/") &&
                    !path.endsWith("-") && !path.startsWith("-") &&
                    !path.endsWith(".") && !path.startsWith(".")
            ) { //avoid /test/ -test- .test.
                if (!path.startsWith("*")) { //avoid *test
                    if (path.contains("/")) {
                        boolean valid = true;
                        int index = 0;
                        String[] split = path.split("/");
                        int length = split.length;
                        for (String s : split) {
                            if (index != length - 1 && s.contains(".")) {
                                valid = false;
                                break;
                            }
                            if (s.isBlank()) {
                                valid = false;
                                break;
                            }
                            if (s.startsWith("-") || s.endsWith("-")) {
                                valid = false;
                                break;
                            }
                            if (s.startsWith(".") || s.endsWith(".")) {
                                valid = false;
                                break;
                            }
                            if (s.contains("*") && !s.equalsIgnoreCase("**")) {
                                valid = false;
                                break;
                            }
                            index++;
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

    public Endpoint expire(String expireReason, TransactionContext context) {
        Validator.validRequiredString(1, 50, expireReason);
        Endpoint updated = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        if (updated.expired) {
            throw new DefinedRuntimeException("endpoint can only expire once", "1042",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!updated.external) {
            throw new DefinedRuntimeException("internal endpoint cannot be expired", "1043",
                HttpResponseCode.BAD_REQUEST);
        }
        if (updated.shared) {
            updated.expired = true;
            updated.expireReason = expireReason;
            context.append(new EndpointExpired(this));
        } else {
            throw new DefinedRuntimeException("only shared endpoint can be expired", "1044",
                HttpResponseCode.BAD_REQUEST);
        }
        updated.setModifiedAt(Instant.now().toEpochMilli());
        updated.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        return updated;
    }

    public Endpoint removeCorsRef() {
        Endpoint updated = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        updated.corsProfileId = null;
        updated.setModifiedAt(Instant.now().toEpochMilli());
        updated.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        return updated;
    }

    public Endpoint removeCacheProfileRef() {
        Endpoint updated = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        updated.cacheProfileId = null;
        updated.setModifiedAt(Instant.now().toEpochMilli());
        updated.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        return updated;
    }

    public boolean sameAs(Endpoint o) {
        return Objects.equals(secured, o.secured) &&
            Objects.equals(description, o.description) &&
            Objects.equals(name, o.name) &&
            Objects.equals(websocket, o.websocket) &&
            Objects.equals(corsProfileId, o.corsProfileId) &&
            Objects.equals(permissionId, o.permissionId) &&
            Objects.equals(cacheProfileId, o.cacheProfileId) &&
            Objects.equals(clientId, o.clientId) &&
            Objects.equals(projectId, o.projectId) &&
            Objects.equals(path, o.path) &&
            Objects.equals(endpointId, o.endpointId) &&
            Objects.equals(method, o.method) &&
            Objects.equals(csrfEnabled, o.csrfEnabled) &&
            Objects.equals(shared, o.shared) &&
            Objects.equals(external, o.external) &&
            Objects.equals(replenishRate, o.replenishRate) &&
            Objects.equals(burstCapacity, o.burstCapacity) &&
            Objects.equals(expired, o.expired) &&
            Objects.equals(expireReason, o.expireReason);
    }
}
