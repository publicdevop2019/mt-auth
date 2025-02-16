package com.mt.access.port.adapter.persistence.revoke_token;

import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.domain.model.revoke_token.RevokeTokenQuery;
import com.mt.access.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class RedisRevokeTokenRepository implements RevokeTokenRepository {
    private static final String REVOKE_TOKEN_PREFIX = "RT:";
    @Autowired
    private RedissonClient redissonClient;

    public SumPagedRep<RevokeToken> query(RevokeTokenQuery query) {
        List<RevokeToken> revokeTokens = new ArrayList<>();
        SumPagedRep<RevokeToken> revokeTokenSumPagedRep = new SumPagedRep<>();
        if (query.getRevokeTokenId() == null || query.getRevokeTokenId().isEmpty()) {
            RKeys keyRef = redissonClient.getKeys();
            Iterable<String> keys = keyRef.getKeysByPattern(REVOKE_TOKEN_PREFIX + "*");
            if (keys != null) {
                long offset =
                    query.getPageConfig().getPageSize() * query.getPageConfig().getPageNumber();
                int count = 0;
                Set<String> outputKey = new HashSet<>();
                for (String str : keys) {
                    if (count >= offset) {
                        outputKey.add(str);
                    }
                    count++;
                }
                for (String str : outputKey) {
                    RBucket<Object> bucket = redissonClient.getBucket(str);
                    String s = (String) bucket.get();
                    RevokeToken deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(s, RevokeToken.class);
                    revokeTokens.add(deserialize);
                }
                revokeTokenSumPagedRep.setTotalItemCount((long) count);
                revokeTokenSumPagedRep.setData(revokeTokens);
            }
        } else {
            query.getRevokeTokenId().forEach(tokenId -> {
                RBucket<Object> bucket =
                    redissonClient.getBucket(REVOKE_TOKEN_PREFIX + tokenId.getDomainId());
                String s = (String) bucket.get();
                if (s != null) {
                    RevokeToken deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(s, RevokeToken.class);
                    revokeTokens.add(deserialize);
                    revokeTokenSumPagedRep.setData(revokeTokens);
                    revokeTokenSumPagedRep.setTotalItemCount(1L);
                }
            });
        }
        return revokeTokenSumPagedRep;
    }

    public void add(RevokeToken revokeToken) {
        String key = REVOKE_TOKEN_PREFIX + revokeToken.getRevokeTokenId().getDomainId();
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(CommonDomainRegistry.getCustomObjectSerializer().serialize(revokeToken));
    }
}
