package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.port.adapter.persistence.client.RedirectUrlConverter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * hibernate projection entity
 * 1. used for oauth login
 * 2. read only
 * 3. eager load
 * 4. subset of client
 */
@Entity
@Immutable
@NoArgsConstructor
@Table(name = "client")
public class ReadOnlyOAuthClient {
    @Id
    @Getter(AccessLevel.PRIVATE)
    private Long id;

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
    private final Set<ClientId> externalResources = new HashSet<>();

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
    @Getter
    private ClientId clientId;

    @Getter
    private String name;

    @Getter
    private String path;

    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "externalUrl"))
    })
    private ExternalUrl externalUrl;

    @Getter
    private String secret;

    @Getter
    private String description;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "client_type_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Set<ClientType> types;

    @Getter
    @Column(name = "accessible_")
    private boolean accessible = false;

    @Getter
    @Embedded
    private OAuthRedirectDetail authorizationCodeGrant;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "client_grant_type_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "grant_type")
    @Enumerated(EnumType.STRING)
    private Set<GrantType> grantTypes;

    @Embedded
    @Getter
    private TokenDetail tokenDetail;

    @Embeddable
    @NoArgsConstructor
    public static class OAuthRedirectDetail{

        @Getter
        @ElementCollection(fetch = FetchType.EAGER)
        @JoinTable(name = "client_redirect_url_map", joinColumns = @JoinColumn(name = "id"))
        @Column(name = "redirect_url")
        @Convert(converter = RedirectUrlConverter.class)
        private final Set<RedirectUrl> redirectUrls = new HashSet<>();

        @Getter
        private boolean autoApprove = false;
    }

    public Integer accessTokenValiditySeconds() {
        if (tokenDetail == null) {
            return null;
        }
        return tokenDetail.getAccessTokenValiditySeconds();
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
}
