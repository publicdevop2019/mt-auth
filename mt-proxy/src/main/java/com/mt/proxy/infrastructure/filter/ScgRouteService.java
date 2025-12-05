package com.mt.proxy.infrastructure.filter;

import com.mt.proxy.domain.Router;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ScgRouteService implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    public void refreshRoutes(Set<Router> routerSet) {
        Set<String> collect = routerSet.stream().filter(e -> e.getPath() != null)
            .map(Router::getId).collect(Collectors.toSet());
        AtomicInteger count = new AtomicInteger();
        collect.forEach(e -> {
            routeDefinitionWriter.delete(Mono.just(e)).subscribe(null, (error) -> {
                count.getAndIncrement();
            });
        });
        if (log.isDebugEnabled()) {
            log.debug("ignore not found ex when delete routes, count {}", count);
        }
        routerSet.stream().filter(app -> app.getPath() != null).forEach(app -> {
            RouteDefinition definition = new RouteDefinition();
            definition.setId(app.getId());
            URI uri = URI.create(app.getExternalUrl());
            definition.setUri(uri);
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName("Path");
            Map<String, String> predicateParams = new HashMap<>(8);
            predicateParams.put("pattern", "/" + app.getPath() + "/**");
            predicate.setArgs(predicateParams);
            definition.setPredicates(Collections.singletonList(predicate));
            FilterDefinition filter = new FilterDefinition();
            filter.setName("RewritePath");
            Map<String, String> filterParams = new HashMap<>(8);
            filterParams.put("regexp", "/" + app.getPath() + "(?<segment>/?.*)");
            if (uri.getPath() != null) {
                filterParams.put("replacement", uri.getPath() + "/${segment}");
            } else {
                filterParams.put("replacement", "${segment}");
            }
            filter.setArgs(filterParams);
            definition.setFilters(List.of(filter));
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        });
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
