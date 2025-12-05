package com.mt.proxy.domain.rate_limit;

import com.mt.proxy.infrastructure.LogService;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Slf4j
@Data
public class RateLimitResult {
    private boolean allowed;
    private Long newTokens;

    public static RateLimitResult deny() {
        RateLimitResult rateLimitResult = new RateLimitResult();
        rateLimitResult.setAllowed(false);
        rateLimitResult.setNewTokens(0L);
        return rateLimitResult;
    }

    public static RateLimitResult error() {
        RateLimitResult rateLimitResult = new RateLimitResult();
        rateLimitResult.setAllowed(false);
        rateLimitResult.setNewTokens(-1L);
        return rateLimitResult;
    }

    public static RateLimitResult alwaysAllow() {
        RateLimitResult rateLimitResult = new RateLimitResult();
        rateLimitResult.setAllowed(true);
        rateLimitResult.setNewTokens(-1L);
        return rateLimitResult;
    }

    public static RateLimitResult parse(List<Long> luaResult, ServerHttpRequest request) {
        if (luaResult.size() != 2 || luaResult.get(0) == null || luaResult.get(1) == null) {
            LogService.reactiveLog(request,
                () -> log.debug("redis script return invalid result {}", luaResult));
            return RateLimitResult.deny();
        } else {
            RateLimitResult rateLimitResult = new RateLimitResult();
            rateLimitResult.setAllowed(luaResult.get(0) == 1L);
            rateLimitResult.setNewTokens(luaResult.get(1));
            LogService.reactiveLog(request,
                () -> log.debug("rate limit allowed {} token {}", rateLimitResult.allowed,
                    rateLimitResult.newTokens));
            return rateLimitResult;
        }
    }
}
