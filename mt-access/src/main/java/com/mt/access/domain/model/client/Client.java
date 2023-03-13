package com.mt.access.domain.model.client;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.event.ClientAccessibilityRemoved;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.access.domain.model.client.event.ClientPathChanged;
import com.mt.access.domain.model.client.event.ClientResourcesChanged;
import com.mt.access.domain.model.client.event.ClientSecretChanged;
import com.mt.access.domain.model.client.event.ClientTokenDetailChanged;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "clientRegion")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"path"}))
public class Client extends Auditable {

    private static final String EMPTY_SECRET = "";
    /**
     * if lazy then loadClientByClientId needs to be transactional
     * use eager as @Transactional is adding too much overhead.
     */
    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "resources_map",
        joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"id", "domainId"})
    )
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(updatable = false, nullable = false))
    })
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "clientResourceRegion")
    private final Set<ClientId> resources = new HashSet<>();
    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "external_resources_map",
        joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"id", "domainId"})
    )
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(updatable = false, nullable = false))
    })
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "clientExtResourceRegion")
    private final Set<ClientId> externalResources = new HashSet<>();
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))
    })
    private ProjectId projectId;
    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "roleId"))
    })
    private RoleId roleId;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private ClientId clientId;
    @Getter
    private String name;
    @Getter
    private String path;
    @Getter
    private String secret;
    @Getter
    private String description;

    @Getter
    @Convert(converter = ClientType.DbConverter.class)
    private Set<ClientType> types;

    @Getter
    @Column(name = "accessible_")
    private boolean accessible = false;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private RedirectDetail authorizationCodeGrant;
    @Convert(converter = GrantType.DbConverter.class)
    @Getter
    private Set<GrantType> grantTypes;

    @Embedded
    @Getter
    private TokenDetail tokenDetail;

    public Client(ClientId clientId,
                  ProjectId projectId,
                  String name,
                  String path,
                  @Nullable String secret,
                  String description,
                  boolean accessible,
                  Set<ClientId> resources,
                  Set<GrantType> grantTypes,
                  TokenDetail tokenDetail,
                  RedirectDetail authorizationCodeGrant,
                  Set<ClientType> types
    ) {
        super();
        setClientId(clientId);
        setProjectId(projectId);
        setResources(resources);
        setDescription(description);
        setAccessible(accessible);
        setName(name);
        setPath(path);
        setTypes(types);
        initSecret(secret);
        setGrantTypes(grantTypes);
        setTokenDetail(tokenDetail);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        //set id last so we know it's new object
        setId(CommonDomainRegistry.getUniqueIdGeneratorService()
            .id());
        setRoleId();
        CommonDomainRegistry.getDomainEventRepository().append(new ClientCreated(this));
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getClientRepository().add(this);
    }

    public void setRoleId() {
        if (this.roleId != null) {
            throw new DefinedRuntimeException("client role cannot be overwritten", "0034",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        this.roleId = new RoleId();
    }

    private void setTypes(Set<ClientType> types) {
        Validator.notEmpty(types);
        if (this.types != null) {
            throw new DefinedRuntimeException("client type can not be updated once created", "0035",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        if (
            types.stream().anyMatch(e -> e.equals(ClientType.FRONTEND_APP))
                && types.stream().anyMatch(e -> e.equals(ClientType.BACKEND_APP))
                ||
                types.stream().anyMatch(e -> e.equals(ClientType.THIRD_PARTY))
                    && types.stream().anyMatch(e -> e.equals(ClientType.FIRST_PARTY))
        ) {
            throw new DefinedRuntimeException("client type conflict", "0036",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        this.types = types;
    }

    private void setPath(String path) {
        if (this.path == null && path == null) {
            return;
        } else if (this.path == null || path == null) {
            CommonDomainRegistry.getDomainEventRepository().append(new ClientPathChanged(clientId));
        } else {
            if (!path.equals(this.path)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientPathChanged(clientId));
            }
        }
        this.path = path;
    }

    private void setGrantTypes(Set<GrantType> grantTypes) {
        if (id != null) {
            if (!ObjectUtils.equals(grantTypes, this.grantTypes)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientGrantTypeChanged(clientId));
            }
        }
        if (grantTypes.contains(GrantType.REFRESH_TOKEN)
            &&
            !grantTypes.contains(GrantType.PASSWORD)) {
            throw new DefinedRuntimeException("refresh token grant requires password grant", "0037",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        this.grantTypes = grantTypes;
    }

    private void setName(String name) {
        Validator.notNull(name);
        String trim = name.trim();
        Validator.notBlank(trim);
        Validator.lengthGreaterThanOrEqualTo(trim, 1);
        Validator.lengthLessThanOrEqualTo(trim, 50);
        Validator.whitelistOnly(trim);
        this.name = trim;
    }

    private void setDescription(String description) {
        if (description != null) {
            String trim = description.trim();
            Validator.lengthLessThanOrEqualTo(trim, 50);
            Validator.whitelistOnly(trim);
            this.description = description;
        }
    }

    private void setTokenDetail(TokenDetail tokenDetail) {
        if (id != null) {
            if (tokenDetailChanged(tokenDetail)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientTokenDetailChanged(clientId));
            }
        }
        this.tokenDetail = tokenDetail;
    }

    private void setAccessible(boolean accessible) {
        if (id != null) {

            if (this.accessible && !accessible) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientAccessibilityRemoved(clientId));
            }
        }
        this.accessible = accessible;
    }

    public void removeResource(ClientId clientId) {
        this.resources.remove(clientId);
    }

    private void setResources(Set<ClientId> resources) {
        if (id != null) {

            if (resourcesChanged(resources)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientResourcesChanged(clientId));
            }
        }
        Validator.notNull(resources);
        if (!resources.equals(this.resources)) {
            this.resources.clear();
            this.resources.addAll(resources);
            DomainRegistry.getClientValidationService()
                .validate(this, new HttpValidationNotificationHandler());
        }
    }

    public void replace(String name,
                        String secret,
                        String path,
                        String description,
                        boolean accessible,
                        Set<ClientId> resources,
                        Set<GrantType> grantTypes,
                        TokenDetail tokenDetail,
                        RedirectDetail authorizationCodeGrant
    ) {
        setPath(path);
        setResources(resources);
        setAccessible(accessible);
        updateSecret(secret);
        setGrantTypes(grantTypes);
        setTokenDetail(tokenDetail);
        setName(name);
        setDescription(description);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        validate(new HttpValidationNotificationHandler());
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new ClientValidator(this, handler)).validate();
    }

    public Endpoint addNewEndpoint(CacheProfileId cacheProfileId,
                                   String name, String description, String path,
                                   EndpointId endpointId, String method,
                                   boolean secured,
                                   boolean isWebsocket, boolean csrfEnabled,
                                   CorsProfileId corsConfig, boolean shared, boolean external,
                                   int replenishRate, int burstCapacity) {
        return new Endpoint(getClientId(), getProjectId(), cacheProfileId,
            name, description, path, endpointId, method, secured,
            isWebsocket, csrfEnabled, corsConfig, shared, external, replenishRate, burstCapacity);
    }

    // for create
    private void initSecret(String secret) {
        Validator.notNull(types);
        Validator.notNull(secret);
        if (types.contains(ClientType.FRONTEND_APP)) {
            secret = EMPTY_SECRET;
        }
        this.secret = DomainRegistry.getEncryptionService().encryptedValue(secret);
    }

    //for update
    private void updateSecret(String secret) {
        if (secret != null && !secret.isBlank()) {
            Validator.notNull(types);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ClientSecretChanged(clientId));
            if (types.contains(ClientType.FRONTEND_APP)) {
                secret = EMPTY_SECRET;
            }
            this.secret = DomainRegistry.getEncryptionService().encryptedValue(secret);
        }
    }

    public int accessTokenValiditySeconds() {
        return tokenDetail.getAccessTokenValiditySeconds();
    }

    public int refreshTokenValiditySeconds() {
        return tokenDetail.getRefreshTokenValiditySeconds();
    }

    private boolean resourcesChanged(Set<ClientId> clientIds) {
        return !ObjectUtils.equals(this.resources, clientIds);
    }

    private boolean tokenDetailChanged(TokenDetail tokenDetail) {
        return !ObjectUtils.equals(this.tokenDetail, tokenDetail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equal(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), clientId);
    }

    public void removeAllReferenced() {
        CommonDomainRegistry.getDomainEventRepository().append(new ClientDeleted(clientId));
        if (isAccessible()) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ClientAsResourceDeleted(clientId));
        }
    }

    public int getRefreshTokenValiditySeconds() {
        if (grantTypes.contains(GrantType.PASSWORD)
            &&
            grantTypes.contains(GrantType.REFRESH_TOKEN)) {
            return getTokenDetail().getRefreshTokenValiditySeconds();
        }
        return 0;
    }

    public boolean getAutoApprove() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getAuthorizationCodeGrant().isAutoApprove();
        }
        return false;
    }

    public Set<String> getRegisteredRedirectUri() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getAuthorizationCodeGrant().getRedirectUrls().stream().map(RedirectUrl::getValue)
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();

    }

    public boolean removable() {
        return types.stream().noneMatch(e -> e.equals(ClientType.ROOT_APPLICATION));
    }

    public void updateExternalResource(Set<ClientId> externalResource) {
        if (!externalResource.equals(this.externalResources)) {
            this.externalResources.clear();
            this.externalResources.addAll(externalResource);
            DomainRegistry.getClientValidationService()
                .validate(this, new HttpValidationNotificationHandler());
        }
    }
}
