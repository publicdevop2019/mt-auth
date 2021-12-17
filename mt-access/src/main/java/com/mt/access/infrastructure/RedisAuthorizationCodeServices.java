package com.mt.access.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Base64;

@Slf4j
@Service
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    private static final String CODE_PREFIX = "AUTHORIZATION_CODE_";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        StoredRequest storedRequest = new StoredRequest(authentication.getUserAuthentication(), authentication.getOAuth2Request());
        byte[] bytes = CommonDomainRegistry.getCustomObjectSerializer()
                .nativeSerialize(storedRequest);
        Base64.Encoder encoder = Base64.getEncoder();
        String s = encoder.encodeToString(bytes);
        String combined = CODE_PREFIX + code;
        log.debug("stored info for {}",code);
        redisTemplate.opsForValue().set(combined, s);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        log.debug("retrieve stored info for {}",code);
        String combined = CODE_PREFIX + code;
        String s = redisTemplate.opsForValue().get(combined);
        redisTemplate.delete(code);
        if (s != null) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decode1 = decoder.decode(s);
            StoredRequest storedRequest = (StoredRequest) CommonDomainRegistry.getCustomObjectSerializer().nativeDeserialize(decode1);
            return new OAuth2Authentication(storedRequest.oAuth2Request, storedRequest.userAuthentication);
        }
        return null;
    }

    @Getter
    private static class StoredRequest implements Serializable {
        private final Authentication userAuthentication;
        private final OAuth2Request oAuth2Request;

        public StoredRequest(Authentication userAuthentication, OAuth2Request oAuth2Request) {
            this.userAuthentication = userAuthentication;
            this.oAuth2Request = oAuth2Request;
        }
    }
}
