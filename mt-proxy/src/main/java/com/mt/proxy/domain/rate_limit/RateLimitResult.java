package com.mt.proxy.domain.rate_limit;

import java.util.List;
import lombok.Data;

@Data
public class RateLimitResult {
    private Boolean allowed;
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

    public static RateLimitResult parse(List<Long> execute) {
        if (execute.get(0) == null) {
            return RateLimitResult.deny();
        } else {
            RateLimitResult rateLimitResult = new RateLimitResult();
            rateLimitResult.setAllowed(execute.get(0) == 1L);
            rateLimitResult.setNewTokens(execute.get(1));
            return rateLimitResult;
        }
    }
}
