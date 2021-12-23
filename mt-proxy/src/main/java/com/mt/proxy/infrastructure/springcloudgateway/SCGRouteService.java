package com.mt.proxy.infrastructure.springcloudgateway;

import com.mt.proxy.domain.RegisteredApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SCGRouteService implements ApplicationEventPublisherAware {
    @Autowired
    RedisRateLimiter redisRateLimiter;
    private ApplicationEventPublisher publisher;
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    public void refreshRoutes(Set<RegisteredApplication> registeredApplicationSet) {
        Set<String> collect = registeredApplicationSet.stream().filter(e -> e.getBasePath() != null).map(e -> e.getId()).collect(Collectors.toSet());
        collect.forEach(e -> {
            routeDefinitionWriter.delete(Mono.just(e)).subscribe(null, (error) -> {
                log.debug("ignore not found ex when delete routes");
            });
        });
        registeredApplicationSet.stream().filter(e -> e.getBasePath() != null).forEach(e -> {
            RouteDefinition definition = new RouteDefinition();
            definition.setId(e.getId());
            definition.setUri(URI.create("lb://" + e.getName()));
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName("Path");
            Map<String, String> predicateParams = new HashMap<>(8);
            predicateParams.put("pattern", "/" + e.getBasePath() + "/**");
            predicate.setArgs(predicateParams);
            definition.setPredicates(Collections.singletonList(predicate));
            FilterDefinition filter = new FilterDefinition();
            filter.setName("RewritePath");
            Map<String, String> filterParams = new HashMap<>(8);
            filterParams.put("regexp", "/" + e.getBasePath() + "(?<segment>/?.*)");
            filterParams.put("replacement", "${segment}");
            filter.setArgs(filterParams);
            FilterDefinition filter2 = new FilterDefinition();
            filter2.setName("RequestRateLimiter");
            Map<String, String> filterParams2 = new HashMap<>(8);
            filterParams2.put("redis-rate-limiter.replenishRate", "50");
            filterParams2.put("redis-rate-limiter.burstCapacity", "100");
            filterParams2.put("rate-limiter", "#{@redisRateLimiter}");
            filterParams2.put("key-resolver", "#{@userKeyResolver}");
            filter2.setArgs(filterParams2);
            definition.setFilters(List.of(filter, filter2));
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        });
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
