package com.mt.proxy.domain.rate_limit;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.Utility;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RateLimitService {
    public static final String RATE_LIMITER = "rate_limiter.";
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisScript<List<Long>> script;

    public RateLimitResult withinRateLimit(String path, String method,
                                           HttpHeaders headers) {
        boolean webSocket = Utility.isWebSocket(headers);
        if (webSocket) {
            return RateLimitResult.alwaysAllow();
        }
        Optional<Endpoint> optionalEndpoint =
            DomainRegistry.getEndpointService().findEndpoint(path, method, false);
        if (optionalEndpoint.isEmpty()) {
            return RateLimitResult.deny();
        }
        Endpoint endpoint = optionalEndpoint.get();

        Endpoint.Subscription subscription;
        String tokenKey;
        if (endpoint.isSecured()) {
            //for protected
            List<String> authorization = headers.get("authorization");
            if (authorization != null && !authorization.isEmpty()) {
                String bearer_ = authorization.get(0).replace("Bearer ", "");
                try {
                    String projectId = DomainRegistry.getJwtService().getProjectId(bearer_);
                    subscription =
                        endpoint.getSubscriptions().stream().filter(e->e.getProjectId().equals(projectId))
                            .findFirst().orElse(null);
                    if (subscription == null) {
                        log.error("unable to find related subscription");
                        return RateLimitResult.deny();
                    }
                    tokenKey = RATE_LIMITER + endpoint.getId() + "." + projectId;
                } catch (ParseException e) {
                    log.error("unable to extract user id and project id");
                    return RateLimitResult.deny();
                }
            } else {
                log.error("unable to find authorization header");
                return RateLimitResult.deny();
            }
        } else {
            //for public endpoint
            tokenKey = RATE_LIMITER + endpoint.getId();
            subscription =
                endpoint.getSelfSubscription();
        }
        return checkLimit(tokenKey, subscription);
    }

    private RateLimitResult checkLimit(String tokenKey, Endpoint.Subscription subscription) {
        RateLimitResult result;
        try {
            List<Long> execute = redisTemplate.execute(script,
                List.of(tokenKey + ".tokens", tokenKey + ".timestamp"),
                String.valueOf(subscription.getReplenishRate()),
                String.valueOf(subscription.getBurstCapacity()),
                String.valueOf(Instant.now().getEpochSecond()));
            result = RateLimitResult.parse(execute);
        } catch (Exception ex) {
            log.error("error during redis script", ex);
            result = RateLimitResult.deny();
        }
        return result;
    }

}
