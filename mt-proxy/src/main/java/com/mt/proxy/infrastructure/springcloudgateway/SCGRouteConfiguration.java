package com.mt.proxy.infrastructure.springcloudgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SCGRouteConfiguration {
    @Autowired
    RedisRateLimiter redisRateLimiter;
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        log.debug("init custom routes");
        RedisRateLimiter.Config config = redisRateLimiter.newConfig();
        config.setBurstCapacity(100);
        config.setReplenishRate(50);
        redisRateLimiter.getConfig().put("oauthModule", config);
        redisRateLimiter.getConfig().put("testModule", config);
        redisRateLimiter.getConfig().put("storeModule", config);
        redisRateLimiter.getConfig().put("sagaModule", config);
        redisRateLimiter.getConfig().put("bbsModule", config);
        redisRateLimiter.getConfig().put("paymentModule", config);
        redisRateLimiter.getConfig().put("fileUploadModule", config);
        redisRateLimiter.getConfig().put("messengerModule", config);
        redisRateLimiter.getConfig().put("productModule", config);
        redisRateLimiter.getConfig().put("profileModule", config);
        return builder.routes()
                .route("oauthModule",
                        p -> p.path("/auth-svc/**")
                                .filters(f ->
                                        f.rewritePath("/auth-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://access"))
                .route("testModule",
                        p -> p.path("/test-svc/**")
                                .filters(f ->
                                        f.rewritePath("/test-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://test"))
                .route("storeModule",
                        p -> p.path("/object-svc/**")
                                .filters(f ->
                                        f.rewritePath("/object-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://store"))
                .route("sagaModule",
                        p -> p.path("/saga-svc/**")
                                .filters(f ->
                                        f.rewritePath("/saga-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://saga"))
                .route("bbsModule",
                        p -> p.path("/bbs-svc/**")
                                .filters(f ->
                                        f.rewritePath("/bbs-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://bbs"))
                .route("paymentModule",
                        p -> p.path("/payment-svc/**")
                                .filters(f ->
                                        f.rewritePath("/payment-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://payment"))
                .route("fileUploadModule",
                        p -> p.path("/file-upload-svc/**")
                                .filters(f ->
                                        f.rewritePath("/file-upload-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://fileUpload"))
                .route("messengerModule",
                        p -> p.path("/messenger-svc/**")
                                .filters(f ->
                                        f.rewritePath("/messenger-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://messenger"))
                .route("productModule",
                        p -> p.path("/product-svc/**")
                                .filters(f ->
                                        f.rewritePath("/product-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://mall"))
                .route("profileModule",
                        p -> p.path("/profile-svc/**")
                                .filters(f ->
                                        f.rewritePath("/profile-svc(?<segment>/?.*)", "${segment}")
                                                .requestRateLimiter(c->c.setRateLimiter(redisRateLimiter)))
                                .uri("lb://profile"))
                .build()

                ;
    }
}
