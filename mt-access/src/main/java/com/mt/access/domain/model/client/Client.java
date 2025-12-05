package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.client.event.ClientSecretChanged;
import com.mt.access.domain.model.client.event.ClientTokenDetailChanged;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Client extends Auditable {
    private static final String MT_ACCESS_ID = "0C8AZTODP4HT";
    private static final String MT_PROXY_ID = "0C8AZYTQ5W5C";
    private static final String MT_UI_REGISTER_ID_ = "0C8B00098WLD";
    private static final String MT_UI_LOGIN_ID = "0C8AZZ16LZB4";
    private static final Set<ClientId> reservedClientIds = new HashSet<>();

    static {
        reservedClientIds.add(new ClientId(MT_ACCESS_ID));
        reservedClientIds.add(new ClientId(MT_PROXY_ID));
        reservedClientIds.add(new ClientId(MT_UI_REGISTER_ID_));
        reservedClientIds.add(new ClientId(MT_UI_LOGIN_ID));
    }

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private ProjectId projectId;

    @Getter
    private RoleId roleId;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private ClientId clientId;

    @Getter
    private String name;

    @Getter
    private String secret;

    @Getter
    private String description;

    @Getter
    private ClientType type;

    @Getter
    private TokenDetail tokenDetail;

    public Client(ClientId clientId, ProjectId projectId, String name,
                  @Nullable String secret, String description,
                  TokenDetail tokenDetail, ClientType type,
                  TransactionContext context
    ) {
        super();
        setClientId(clientId);
        setProjectId(projectId);
        setDescription(description);
        setName(name);
        initType(type);
        initSecret(secret);
        setTokenDetail(tokenDetail, context);
        setRoleId();
        //set id last so we know it's new object
        //TOOD try rm above comment
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        context.append(new ClientCreated(this));
        validate(new HttpValidationNotificationHandler());
        long milli = Instant.now().toEpochMilli();
        setCreatedAt(milli);
        setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        setModifiedAt(milli);
        setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        DomainRegistry.getClientRepository().add(this);
    }

    public static Client fromDatabaseRow(Long id, Long createdAt, String createdBy, Long modifiedAt,
                                         String modifiedBy, Integer version, ClientId domainId,
                                         String description, String name,
                                         ClientType type, ProjectId projectId, RoleId roleId,
                                         String secret,
                                         Integer accessTokenValiditySeconds,
                                         Integer refreshTokenValiditySeconds
    ) {
        Client client = new Client();
        client.setId(id);
        client.setCreatedAt(createdAt);
        client.setCreatedBy(createdBy);
        client.setModifiedAt(modifiedAt);
        client.setModifiedBy(modifiedBy);
        client.setVersion(version);
        client.setDescription(description);
        client.setName(name);
        client.setClientId(domainId);
        client.setProjectId(projectId);
        client.type = type;
        client.roleId = roleId;
        client.secret = secret;
        client.tokenDetail =
            new TokenDetail(accessTokenValiditySeconds, refreshTokenValiditySeconds);
        return client;
    }

    private void setRoleId() {
        if (this.roleId != null) {
            throw new DefinedRuntimeException("client role cannot be overwritten", "1034",
                HttpResponseCode.BAD_REQUEST);
        }
        this.roleId = new RoleId();
    }

    private void initType(ClientType type) {
        Validator.notNull(type);
        if (Checker.notNull(this.type)) {
            throw new DefinedRuntimeException("client type can not be updated once created", "1035",
                HttpResponseCode.BAD_REQUEST);
        }
        this.type = type;
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name.trim();
    }

    private void setDescription(String description) {
        Validator.validOptionalString(50, description);
        if (Checker.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
    }

    private void setTokenDetail(TokenDetail tokenDetail, TransactionContext context) {
        if (id != null) {
            if (tokenDetailChanged(tokenDetail)) {
                context
                    .append(new ClientTokenDetailChanged(clientId));
            }
        }
        this.tokenDetail = tokenDetail;
    }

    public Client update(
        String name, String secret, String description,
        TokenDetail tokenDetail, TransactionContext context
    ) {
        Client updated = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        updated.updateSecret(secret, context);
        updated.setTokenDetail(tokenDetail, context);
        updated.setName(name);
        updated.setDescription(description);
        updated.validate(new HttpValidationNotificationHandler());
        updated.setModifiedAt(Instant.now().toEpochMilli());
        updated.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        DomainRegistry.getClientRepository().update(this, updated);
        return updated;
    }

    @Override
    public void validate(ValidationNotificationHandler handler) {
        (new ClientValidator(this, handler)).validate();
    }

    //for create
    private void initSecret(String secret) {
        Validator.notNull(secret);
        Validator.notBlank(secret);
        this.secret = secret;
    }

    //for update
    private void updateSecret(String secret, TransactionContext context) {
        Validator.notNull(secret);
        Validator.notBlank(secret);
        if (!Checker.equals(secret, this.secret)) {
            context
                .append(new ClientSecretChanged(clientId));
            this.secret = secret;
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

    private boolean tokenDetailChanged(TokenDetail tokenDetail) {
        return !Checker.equals(this.tokenDetail, tokenDetail);
    }

    public void remove(TransactionContext context) {
        if (reservedClientIds.contains(this.clientId)) {
            throw new DefinedRuntimeException("client cannot be deleted", "1009",
                HttpResponseCode.BAD_REQUEST);
        }
        DomainRegistry.getClientRepository().remove(this);
        context.append(new ClientDeleted(clientId));
    }

    public boolean sameAs(Client o) {
        return
            Objects.equals(projectId, o.projectId) &&
                Objects.equals(roleId, o.roleId) &&
                Objects.equals(clientId, o.clientId) &&
                Objects.equals(name, o.name) &&
                Objects.equals(secret, o.secret) &&
                Objects.equals(description, o.description) &&
                Objects.equals(type, o.type) &&
                Objects.equals(tokenDetail, o.tokenDetail);
    }

    public int getRefreshTokenValiditySeconds(Set<GrantType> grantTypes) {
        if (grantTypes.contains(GrantType.PASSWORD)
            &&
            grantTypes.contains(GrantType.REFRESH_TOKEN)) {
            return getTokenDetail().getRefreshTokenValiditySeconds();
        }
        return 0;
    }
}
