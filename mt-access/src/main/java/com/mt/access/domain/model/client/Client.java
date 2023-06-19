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
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientRegion")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"path"}))
public class Client extends Auditable {
    private static final String MT_ACCESS_ID = "0C8AZTODP4HT";
    private static final String MT_PROXY_ID = "0C8AZYTQ5W5C";
    private static final String MT_UI_REGISTER_ID_ = "0C8B00098WLD";
    private static final String MT_UI_LOGIN_ID = "0C8AZZ16LZB4";
    private static final String EMPTY_SECRET = "";
    private static final Set<ClientId> reservedClientIds = new HashSet<>();
    private static final Pattern PATH_REGEX = Pattern.compile("^[a-z\\-/]*$");

    static {
        reservedClientIds.add(new ClientId(MT_ACCESS_ID));
        reservedClientIds.add(new ClientId(MT_PROXY_ID));
        reservedClientIds.add(new ClientId(MT_UI_REGISTER_ID_));
        reservedClientIds.add(new ClientId(MT_UI_LOGIN_ID));
    }

    /**
     * if lazy then loadClientByClientId needs to be transactional
     * use eager to avoid @Transactional adding too much overhead.
     */
    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "resources_map", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"), uniqueConstraints = @UniqueConstraint(columnNames = {
        "id", "domainId"}))
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(updatable = false, nullable = false))})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientResourceRegion")
    private Set<ClientId> resources;

    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "external_resources_map", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"), uniqueConstraints = @UniqueConstraint(columnNames = {
        "id", "domainId"}))
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(updatable = false, nullable = false))})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientExtResourceRegion")
    private Set<ClientId> externalResources;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))})
    private ProjectId projectId;

    @Getter
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "domainId", column = @Column(name = "roleId"))})
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
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "externalUrl"))})
    private ExternalUrl externalUrl;

    @Getter
    private String secret;

    @Getter
    private String description;

    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "client_type_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "type")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientTypeRegion")
    @Enumerated(EnumType.STRING)
    private Set<ClientType> types;

    @Getter
    @Column(name = "accessible_")
    private Boolean accessible;

    @Getter
    @Embedded
    private RedirectDetail redirectDetail;

    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "client_grant_type_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "grant_type")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "clientGrantTypeRegion")
    @Enumerated(EnumType.STRING)
    private Set<GrantType> grantTypes;

    @Embedded
    @Getter
    private TokenDetail tokenDetail;

    public Client(ClientId clientId, ProjectId projectId, String name, String path,
                  @Nullable String secret, String description, Boolean accessible,
                  Set<ClientId> resources, Set<GrantType> grantTypes, TokenDetail tokenDetail,
                  Set<String> redirectUrls, Boolean autoApprove, Set<ClientType> types,
                  ExternalUrl externalUrl) {
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
        setGrantTypes(grantTypes, true);
        setTokenDetail(tokenDetail);
        setRedirectDetail(redirectUrls, autoApprove);
        setRoleId();
        setExternalUrl(externalUrl);
        //set id last so we know it's new object
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        CommonDomainRegistry.getDomainEventRepository().append(new ClientCreated(this));
        DomainRegistry.getClientRepository().add(this);
        validate(new HttpValidationNotificationHandler());
    }

    private void setExternalUrl(ExternalUrl externalUrl) {
        if (this.externalUrl == null && externalUrl == null) {
            return;
        } else if (this.externalUrl == null || externalUrl == null) {
            CommonDomainRegistry.getDomainEventRepository().append(new ClientPathChanged(clientId));
        } else {
            if (!externalUrl.equals(this.externalUrl)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientPathChanged(clientId));
            }
        }
        this.externalUrl = externalUrl;
    }

    public void setRedirectDetail(Set<String> redirectUrls, Boolean autoApprove) {
        RedirectDetail redirectDetail;
        if (Checker.isNull(redirectUrls) && Checker.isNull(autoApprove)) {
            redirectDetail = null;
        } else {
            redirectDetail = new RedirectDetail(redirectUrls, autoApprove);
        }
        if (this.redirectDetail == null) {
            this.redirectDetail = redirectDetail;
        } else if (!this.redirectDetail.equals(redirectDetail)) {
            //@todo find better fix
            //hibernate will create redirectDetail with empty values after read from DB even no such information
            //since we are passing null when redirect urls and auto approve are null
            //this will create unnecessary update to DB
            //below logic is added to avoid this update
            if (Checker.isNull(this.redirectDetail.getAutoApprove()) &&
                (Checker.isNull(this.redirectDetail.getRedirectUrls()) ||
                    Checker.isEmpty(this.redirectDetail.getRedirectUrls()))) {
                return;
            }
            this.redirectDetail = redirectDetail;
        }
    }

    public void setRoleId() {
        if (this.roleId != null) {
            throw new DefinedRuntimeException("client role cannot be overwritten", "1034",
                HttpResponseCode.BAD_REQUEST);
        }
        this.roleId = new RoleId();
    }

    private void setTypes(Set<ClientType> types) {
        Validator.notNull(types);
        Validator.notEmpty(types);
        if (this.types != null) {
            throw new DefinedRuntimeException("client type can not be updated once created", "1035",
                HttpResponseCode.BAD_REQUEST);
        }
        if (types.stream().anyMatch(e -> e.equals(ClientType.FRONTEND_APP)) &&
            types.stream().anyMatch(e -> e.equals(ClientType.BACKEND_APP))) {
            throw new DefinedRuntimeException("client type conflict", "1036",
                HttpResponseCode.BAD_REQUEST);
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
        if (Checker.notNull(path)) {
            Validator.lessThanOrEqualTo(path, 50);
            Validator.greaterThanOrEqualTo(path, 5);
            Matcher matcher = PATH_REGEX.matcher(path);//alpha - / only
            boolean result = false;
            if (matcher.find()) {
                if (!path.startsWith("/") && !path.endsWith("/")) { //avoid /test/
                    if (!path.endsWith("-") && !path.startsWith("-")) { //avoid -test-
                        if (path.contains("/")) {
                            boolean valid = true;
                            for (String s : path.split("/")) {
                                if (s.startsWith("-") || s.endsWith("-")) {
                                    valid = false;
                                    break;
                                }
                                if (s.isBlank()) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (valid) {
                                result = true;
                            }
                        } else {
                            result = true;
                        }
                    }
                }
            }
            if (!result) {
                throw new DefinedRuntimeException("invalid path format", "1084",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        this.path = path;
    }

    private void setGrantTypes(Set<GrantType> grantTypes, boolean isCreate) {
        Validator.notNull(grantTypes);
        Validator.notEmpty(grantTypes);
        if (!isCreate) {
            if (!ObjectUtils.equals(grantTypes, this.grantTypes)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientGrantTypeChanged(clientId));
            }
        }
        if (grantTypes.contains(GrantType.REFRESH_TOKEN) &&
            !grantTypes.contains(GrantType.PASSWORD)) {
            throw new DefinedRuntimeException("refresh token grant requires password grant", "1037",
                HttpResponseCode.BAD_REQUEST);
        }
        CommonUtility.updateCollection(this.grantTypes, grantTypes,
            () -> this.grantTypes = grantTypes);
    }

    private void setName(String name) {
        Validator.validRequiredString(5, 50, name);
        this.name = name.trim();
    }

    private void setDescription(String description) {
        Validator.validOptionalString(50, description);
        if (Checker.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
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

    private void setAccessible(Boolean accessible) {
        if (Checker.notNull(id)) {
            if (Checker.isTrue(getAccessible()) && Checker.isFalse(accessible)) {
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
        if (Checker.notNull(resources)) {
            Validator.lessThanOrEqualTo(resources, 10);
        }
        if (Checker.notNull(id)) {
            if (resourcesChanged(resources)) {
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new ClientResourcesChanged(clientId));
            }
        }
        Validator.notNull(resources);
        if (CommonUtility.collectionWillChange(this.resources, resources)) {
            CommonUtility.updateCollection(this.resources, resources, () -> this.resources = resources);
            DomainRegistry.getClientValidationService()
                .validate(this, new HttpValidationNotificationHandler());
        }
    }

    public void replace(String name, String secret, String path, String description,
                        Boolean accessible, Set<ClientId> resources, Set<GrantType> grantTypes,
                        TokenDetail tokenDetail, Set<String> redirectUrl, Boolean autoApprove,
                        ExternalUrl externalUrl) {
        setPath(path);
        setResources(resources);
        setAccessible(accessible);
        updateSecret(secret);
        setGrantTypes(grantTypes, false);
        setTokenDetail(tokenDetail);
        setName(name);
        setDescription(description);
        setRedirectDetail(redirectUrl, autoApprove);
        setExternalUrl(externalUrl);
        validate(new HttpValidationNotificationHandler());
    }

    public void replace(String name, String secret, String path, String description,
                        Boolean accessible, Set<ClientId> resources, Set<GrantType> grantTypes,
                        TokenDetail tokenDetail) {
        setName(name);
        setDescription(description);
        setPath(path);
        setResources(resources);
        setAccessible(accessible);
        updateSecret(secret);
        setGrantTypes(grantTypes, false);
        setTokenDetail(tokenDetail);
        validate(new HttpValidationNotificationHandler());
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new ClientValidator(this, handler)).validate();
    }

    public Endpoint addNewEndpoint(CacheProfileId cacheProfileId, String name, String description,
                                   String path, EndpointId endpointId, String method,
                                   Boolean secured, Boolean websocket, Boolean csrfEnabled,
                                   CorsProfileId corsConfig, Boolean shared, Boolean external,
                                   Integer replenishRate, Integer burstCapacity) {
        return new Endpoint(getClientId(), getProjectId(), cacheProfileId, name, description, path,
            endpointId, method, secured, websocket, csrfEnabled, corsConfig, shared, external,
            replenishRate, burstCapacity);
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

    public Integer accessTokenValiditySeconds() {
        if (tokenDetail == null) {
            return null;
        }
        return tokenDetail.getAccessTokenValiditySeconds();
    }

    public Integer refreshTokenValiditySeconds() {
        if (tokenDetail == null) {
            return null;
        }
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
        if (Checker.isTrue(getAccessible())) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ClientAsResourceDeleted(clientId));
        }
    }

    public Boolean getAutoApprove() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getRedirectDetail().getAutoApprove();
        }
        return false;
    }

    public boolean removable() {
        return !reservedClientIds.contains(this.clientId);
    }

    public void updateExternalResource(Set<ClientId> externalResource) {
        if (CommonUtility.collectionWillChange(this.externalResources, externalResource)) {
            CommonUtility.updateCollection(this.externalResources, externalResource,
                () -> this.externalResources = externalResource);
            DomainRegistry.getClientValidationService()
                .validate(this, new HttpValidationNotificationHandler());
        }
    }
}
