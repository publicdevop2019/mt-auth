package com.mt.access.port.adapter.persistence.revoke_token;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.domain.model.revoke_token.RevokeTokenQuery;
import com.mt.access.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Repository
public class RedisRevokeTokenRepository implements RevokeTokenRepository {
    private static final String REVOKE_TOKEN_PREFIX = "RT:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    public SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery query) {
        return QueryBuilderRegistry.getRedisRevokeTokenAdaptor().execute(query, redisTemplate);
    }

    public void add(RevokeToken revokeToken) {
        redisTemplate.opsForValue().set(REVOKE_TOKEN_PREFIX + revokeToken.getRevokeTokenId().getDomainId(), CommonDomainRegistry.getCustomObjectSerializer().serialize(revokeToken));
    }

    @Component
    public static class RedisRevokeTokenAdaptor {
        public SumPagedRep<RevokeToken> execute(RevokeTokenQuery query, StringRedisTemplate redisTemplate) {
            List<RevokeToken> revokeTokens = new ArrayList<>();
            SumPagedRep<RevokeToken> revokeTokenSumPagedRep = new SumPagedRep<>();
            if (query.getRevokeTokenId() == null || query.getRevokeTokenId().isEmpty()) {
                Set<String> keys = redisTemplate.keys(REVOKE_TOKEN_PREFIX + "*");
                if (keys != null) {
                    long offset = query.getPageConfig().getPageSize() * query.getPageConfig().getPageNumber();
                    int count = 0;
                    Set<String> outputKey = new HashSet<>();
                    for (String str : keys) {
                        if (count >= offset) {
                            outputKey.add(str);
                        }
                        if (outputKey.size() == query.getPageConfig().getPageSize())
                            break;
                        count++;
                    }
                    for (String str : outputKey) {
                        String s = redisTemplate.opsForValue().get(str);
                        RevokeToken deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(s, RevokeToken.class);
                        revokeTokens.add(deserialize);
                    }
                    revokeTokenSumPagedRep.setTotalItemCount((long) keys.size());
                    revokeTokenSumPagedRep.setData(revokeTokens);
                }
            } else {
                query.getRevokeTokenId().forEach(tokenId -> {
                    String s = redisTemplate.opsForValue().get(REVOKE_TOKEN_PREFIX + tokenId.getDomainId());
                    if (s != null) {
                        RevokeToken deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(s, RevokeToken.class);
                        revokeTokens.add(deserialize);
                        revokeTokenSumPagedRep.setData(revokeTokens);
                        revokeTokenSumPagedRep.setTotalItemCount(1L);
                    }
                });
            }
            return revokeTokenSumPagedRep;
        }
    }
}
