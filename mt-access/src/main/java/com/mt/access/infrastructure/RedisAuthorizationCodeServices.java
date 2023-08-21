package com.mt.access.infrastructure;

import com.mt.access.domain.model.token.AuthorizeInfo;
import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import java.util.Base64;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisAuthorizationCodeServices {
    private static final String CODE_PREFIX = "AUTHORIZATION_CODE_";
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void store(String code, AuthorizeInfo authorizeInfo) {
        byte[] bytes = CommonDomainRegistry.getCustomObjectSerializer()
            .nativeSerialize(authorizeInfo);
        if (log.isDebugEnabled()) {
            String serialize =
                CommonDomainRegistry.getCustomObjectSerializer().serialize(authorizeInfo);
            log.debug("stored {}", serialize);
        }
        Base64.Encoder encoder = Base64.getEncoder();
        String s = encoder.encodeToString(bytes);
        String combined = CODE_PREFIX + code;
        log.debug("stored info for {}", code);
        redisTemplate.opsForValue().set(combined, s);
    }

    public AuthorizeInfo remove(String code) {
        log.debug("retrieve stored info for {}", code);
        String combined = CODE_PREFIX + code;
        String value = redisTemplate.opsForValue().get(combined);
        redisTemplate.delete(code);
        if (value != null) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decoded = decoder.decode(value);
            AuthorizeInfo retrieved =
                (AuthorizeInfo) CommonDomainRegistry.getCustomObjectSerializer()
                    .nativeDeserialize(decoded);
            if (log.isDebugEnabled()) {
                String serialize =
                    CommonDomainRegistry.getCustomObjectSerializer().serialize(retrieved);
                log.debug("retrieved {}", serialize);
            }
            return retrieved;
        }
        return null;
    }
}
