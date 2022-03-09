package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
public class ClientSpringOAuth2Representation implements ClientDetails {
    private ClientId clientId;
    private String clientSecret;
    private Set<GrantType> grantTypeEnums;
    private int accessTokenValiditySeconds;
    private Set<String> registeredRedirectUri;
    private int refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private boolean autoApprove = false;

    public ClientSpringOAuth2Representation(Client client) {
        setClientId(client.getClientId());
        setClientSecret(client.getSecret());
        setGrantTypeEnums(client.getGrantTypes());
        setAccessTokenValiditySeconds(client.accessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        Set<String> collect = client.getResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        Set<String> collect2 = client.getExternalResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        collect2.addAll(collect);
        setResourceIds(collect2);
        setAutoApprove(client.getAutoApprove());
        setRegisteredRedirectUri(client.getRegisteredRedirectUri());

    }

    @Override
    public String getClientId() {
        return clientId.getDomainId();
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return grantTypeEnums.contains(GrantType.AUTHORIZATION_CODE);
    }

    @Override
    public Set<String> getScope() {
//        if (isScoped()) {
//            return scope.stream().map(DomainId::getDomainId).collect(Collectors.toSet());
//        } else {
//            return Collections.emptySet();
//        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return grantTypeEnums.stream().map(e -> e.name().toLowerCase()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return autoApprove;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }


}
