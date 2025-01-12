package com.mt.access.port.adapter.persistence.token;

import com.mt.access.domain.model.token.AuthorizationCodeRepository;
import com.mt.access.domain.model.token.AuthorizeInfo;
import com.mt.common.domain.CommonDomainRegistry;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisAuthorizationCodeRepository implements AuthorizationCodeRepository {
    private static final String CODE_PREFIX = "AUTHORIZATION_CODE_";
    @Autowired
    private RedissonClient redissonClient;

    public void store(String code, AuthorizeInfo authorizeInfo) {
        byte[] bytes = CommonDomainRegistry.getCustomObjectSerializer()
            .nativeSerialize(authorizeInfo);
        if (log.isDebugEnabled()) {
            String serialize =
                CommonDomainRegistry.getCustomObjectSerializer().serialize(authorizeInfo);
            log.debug("stored {}", serialize);
        }
        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(bytes);
        String key = CODE_PREFIX + code;
        log.debug("stored info for {}", code);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(encoded);
    }

    public AuthorizeInfo remove(String code) {
        log.debug("retrieve stored info for {}", code);
        String key = CODE_PREFIX + code;
        RBucket<String> bucket = redissonClient.getBucket(key);
        String value = bucket.getAndDelete();
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
