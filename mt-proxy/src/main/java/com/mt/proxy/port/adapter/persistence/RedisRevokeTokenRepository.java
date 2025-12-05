package com.mt.proxy.port.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.proxy.domain.RevokeToken;
import com.mt.proxy.domain.RevokeTokenRepository;
import com.mt.proxy.domain.exception.DefinedRuntimeException;
import com.mt.proxy.domain.exception.HttpResponseCode;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RedisRevokeTokenRepository implements RevokeTokenRepository {
    private static final String REVOKE_TOKEN_PREFIX = "RT:";
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ObjectMapper mapper;

    public Optional<RevokeToken> revokeToken(String id) {
        RBucket<Object> bucket = redissonClient.getBucket(REVOKE_TOKEN_PREFIX + id);
        String cache = (String) bucket.get();
        if (cache != null) {
            try {
                return Optional.of(mapper.readValue(cache, RevokeToken.class));
            } catch (IOException e) {
                log.error("error during deserialize revoke token", e);
                throw new DefinedRuntimeException("deserialize revoke token failed", "2001",
                    HttpResponseCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            return Optional.empty();
        }
    }

}
