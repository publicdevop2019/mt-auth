package com.mt.access.infrastructure.oauth2;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class JwtCustomHeadersAccessTokenConverter extends JwtAccessTokenConverter {

    final RsaSigner signer;
    private final Map<String, String> customHeaders;
    private final JsonParser objectMapper = JsonParserFactory.create();

    public JwtCustomHeadersAccessTokenConverter(Map<String, String> customHeaders,
                                                KeyPair keyPair) {
        super();
        super.setKeyPair(keyPair);
        this.signer = new RsaSigner((RSAPrivateKey) keyPair.getPrivate());
        this.customHeaders = customHeaders;
    }

    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String content;
        try {
            Map<String, ?> stringMap =
                getAccessTokenConverter().convertAccessToken(accessToken, authentication);
            stringMap.remove("authorities");
            content = this.objectMapper.formatMap(stringMap);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot convert access token to JSON", ex);
        }
        return JwtHelper.encode(content, this.signer, this.customHeaders)
            .getEncoded();
    }
}
