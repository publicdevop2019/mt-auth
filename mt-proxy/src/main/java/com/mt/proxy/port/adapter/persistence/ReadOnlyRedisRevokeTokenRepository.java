package com.mt.proxy.port.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.proxy.domain.RevokeToken;
import com.mt.proxy.domain.RevokeTokenRepository;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ReadOnlyRedisRevokeTokenRepository implements RevokeTokenRepository {
    private static final String REVOKE_TOKEN_PREFIX = "RT:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper mapper;

    public Optional<RevokeToken> revokeToken(String id) {
        String cache = redisTemplate.opsForValue().get(REVOKE_TOKEN_PREFIX + id);
        if (cache != null) {
            try {
                return Optional.of(mapper.readValue(cache, RevokeToken.class));
            } catch (IOException e) {
                log.error("error during deserialize revoke token", e);
                throw new RevokeTokenDeserializeException();
            }
        } else {
            return Optional.empty();
        }
    }

    public static class RevokeTokenDeserializeException extends RuntimeException {
    }
}
