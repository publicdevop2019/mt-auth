package com.mt.proxy.domain.rate_limit;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.Utility;
import com.mt.proxy.infrastructure.LogService;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RateLimitService {
    public static final String RATE_LIMITER = "rate_limiter";
    private static final String LUA_SCRIPT =
        "local tokens_key = KEYS[1]\n" +
            "local timestamp_key = KEYS[2]\n" +
            "local rate = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local fill_time = capacity/rate\n" +
            "local ttl = math.floor(fill_time*2)\n" +
            "local last_tokens = tonumber(redis.call(\"get\", tokens_key))\n" +
            "if last_tokens == nil then\n" +
            "  last_tokens = capacity\n" +
            "end\n" +
            "local last_refreshed = tonumber(redis.call(\"get\", timestamp_key))\n" +
            "if last_refreshed == nil then\n" +
            "  last_refreshed = 0\n" +
            "end\n" +
            "local delta = math.max(0, now-last_refreshed)\n" +
            "local filled_tokens = math.min(capacity, last_tokens+(delta*rate))\n" +
            "local allowed = filled_tokens >= 1\n" +
            "local new_tokens = filled_tokens\n" +
            "if allowed then\n" +
            "  new_tokens = filled_tokens-1\n" +
            "end\n" +
            "redis.call(\"setex\", tokens_key, ttl, new_tokens)\n" +
            "redis.call(\"setex\", timestamp_key, ttl, now)\n" +
            "return { allowed, new_tokens }";
    @Autowired
    private RedissonClient redissonClient;

    public RateLimitResult withinRateLimit(ServerHttpRequest request, String path, String method,
                                           HttpHeaders headers, InetSocketAddress address) {
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
        if (endpoint.getSecured()) {
            //for protected
            String authorization = headers.getFirst("authorization");
            if (authorization != null) {
                String bearer_ = authorization.replace("Bearer ", "");
                try {
                    String projectId = DomainRegistry.getJwtService().getProjectId(bearer_);
                    String userId = DomainRegistry.getJwtService().getUserId(bearer_);
                    subscription =
                        endpoint.getSubscriptions().stream()
                            .filter(e -> e.getProjectId().equals(projectId))
                            .findFirst().orElse(null);
                    if (subscription == null) {
                        log.error("unable to find related subscription");
                        return RateLimitResult.deny();
                    }
                    tokenKey = String.join(".", RATE_LIMITER, endpoint.getId(), projectId, userId);
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
            String ip = address.getAddress().getHostAddress().replace(".", "_");
            tokenKey = String.join(".", RATE_LIMITER, endpoint.getId(), ip);
            subscription =
                endpoint.getSelfSubscription();
        }
        return checkLimit(request, tokenKey, subscription);
    }

    private RateLimitResult checkLimit(ServerHttpRequest request, String tokenKey,
                                       Endpoint.Subscription subscription) {
        RateLimitResult result;
        try {
            RScript script = redissonClient.getScript(LongCodec.INSTANCE);
            Integer replenishRate = subscription.getReplenishRate();
            Integer burstCapacity = subscription.getBurstCapacity();
            long epochSecond = Instant.now().getEpochSecond();
            List<Long> luaResult = script.eval(
                RScript.Mode.READ_WRITE,
                LUA_SCRIPT,
                RScript.ReturnType.MULTI,
                List.of(tokenKey + ".tokens", tokenKey + ".timestamp"),
                replenishRate,
                burstCapacity,
                epochSecond
            );
            if (luaResult == null) {
                LogService.reactiveLog(request, () -> log.error("redis script return null"));
                return RateLimitResult.deny();
            }
            result = RateLimitResult.parse(luaResult, request);
        } catch (Exception ex) {
            LogService.reactiveLog(request, () -> log.error("error during redis script", ex));
            result = RateLimitResult.error();
        }
        return result;
    }

}
