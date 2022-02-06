package com.mt.access.domain.model.client;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.event.*;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table
@Entity
@NoArgsConstructor
@Where(clause = "deleted=0")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientRegion")
public class Client extends Auditable {

    /**
     * if lazy then loadClientByClientId needs to be transactional
     * use eager as @Transactional is adding too much overhead
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
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientResourceRegion")
    private final Set<ClientId> resources = new HashSet<>();
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))
    })
    private ProjectId projectId;
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
    @Convert(converter = ClientType.DBConverter.class)
    private Set<ClientType> types;

    @Getter
    @Column(name = "accessible_")
    private boolean accessible = false;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private RedirectDetail authorizationCodeGrant;
    @Convert(converter = GrantType.DBConverter.class)
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
        setClientId(clientId);
        setProjectId(projectId);
        setResources(resources);
        setDescription(description);
        setAccessible(accessible);
        setName(name);
        setPath(path);
        setTypes(types);
        setSecret(secret);
        setGrantTypes(grantTypes);
        setTokenDetail(tokenDetail);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());//set id last so we know it's new object
        DomainEventPublisher.instance().publish(new ClientCreated(clientId));
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getClientRepository().add(this);
    }

    private void setTypes(Set<ClientType> types) {
        if (this.types != null)
            throw new IllegalArgumentException("type can not be updated");
        this.types = types;
    }

    private void setPath(String path) {
        if (this.path == null && path == null) {
            return;
        } else if (this.path == null || path == null) {
            DomainEventPublisher.instance().publish(new ClientPathChanged(clientId));
        } else {
            if (!path.equals(this.path)) {
                DomainEventPublisher.instance().publish(new ClientPathChanged(clientId));
            }
        }
        this.path = path;
    }

    private void setGrantTypes(Set<GrantType> grantTypes) {
        if (id != null) {
            if (!ObjectUtils.equals(grantTypes, this.grantTypes)) {
                DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(clientId));
            }
        }
        if (grantTypes.contains(GrantType.REFRESH_TOKEN) && !grantTypes.contains(GrantType.PASSWORD))
            throw new IllegalArgumentException("refresh token grant requires password grant");
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
                DomainEventPublisher.instance().publish(new ClientTokenDetailChanged(clientId));
            }
        }
        this.tokenDetail = tokenDetail;
    }

    private void setAccessible(boolean accessible) {
        if (id != null) {

            if (this.accessible && !accessible) {
                DomainEventPublisher.instance().publish(new ClientAccessibilityRemoved(clientId));
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
                DomainEventPublisher.instance().publish(new ClientResourcesChanged(clientId));
            }
        }
        Validator.notNull(resources);
        if (!resources.equals(this.resources)) {
            this.resources.clear();
            this.resources.addAll(resources);
            DomainRegistry.getClientValidationService().validate(this, new HttpValidationNotificationHandler());
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
        setSecret(secret);
        setGrantTypes(grantTypes);
        setTokenDetail(tokenDetail);
        setName(name);
        setDescription(description);
        setAuthorizationCodeGrant(authorizationCodeGrant);
        validate(new HttpValidationNotificationHandler());
        DomainEventPublisher.instance().publish(new ClientUpdated(getClientId()));
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new ClientValidator(this, handler)).validate();
    }

    public Endpoint addNewEndpoint(@Nullable PermissionId permissionId, CacheProfileId cacheProfileId,
                                   String description, String path, EndpointId endpointId, String method,
                                   boolean secured,
                                   boolean isWebsocket, boolean csrfEnabled, CORSProfileId corsConfig) {
        return new Endpoint(getClientId(), getProjectId(), permissionId, cacheProfileId,
                description, path, endpointId, method, secured,
                isWebsocket, csrfEnabled, corsConfig);
    }

    private void setSecret(String secret) {
        if (id != null) {
            if (secretChanged(secret)) {
                DomainEventPublisher.instance().publish(new ClientSecretChanged(clientId));
            }
        }
        if (StringUtils.hasText(secret))
            this.secret = DomainRegistry.getEncryptionService().encryptedValue(secret);
    }

    private boolean secretChanged(String secret) {
        return StringUtils.hasText(secret);
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
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equal(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), clientId);
    }

    public void removeAllReferenced() {
        DomainEventPublisher.instance().publish(new ClientDeleted(clientId));
        if (isAccessible()) {
            DomainEventPublisher.instance().publish(new ClientAsResourceDeleted(clientId));
        }
    }

    public int getRefreshTokenValiditySeconds() {
        if (grantTypes.contains(GrantType.PASSWORD) && grantTypes.contains(GrantType.REFRESH_TOKEN)) {
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
            return getAuthorizationCodeGrant().getRedirectUrls().stream().map(RedirectURL::getValue).collect(Collectors.toSet());
        }
        return Collections.emptySet();

    }

    public boolean removable() {
        return types.stream().noneMatch(e -> e.equals(ClientType.ROOT_APPLICATION));
    }
}
