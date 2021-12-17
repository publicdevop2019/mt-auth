package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.CheckSumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class EndpointService {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private Set<Endpoint> cached = new HashSet<>();
    @Autowired
    private CsrfService csrfService;
    @Autowired
    private CorsService corsService;
    @Autowired
    private CacheService cacheService;

    public static Optional<Endpoint> getMostSpecificSecurityProfile(List<Endpoint> collect1, String requestURI) {
        Optional<Endpoint> next;
        if (collect1.size() == 1) {
            next = Optional.of(collect1.get(0));
        } else {
            List<Endpoint> exactMatch = collect1.stream().filter(e -> !e.getPath().contains("/**")).collect(Collectors.toList());
            if (exactMatch.size() == 1) {
                next = Optional.of(exactMatch.get(0));
            } else {
                List<Endpoint> collect2 = collect1.stream().filter(e -> !e.getPath().endsWith("/**")).collect(Collectors.toList());
                if (collect2.size() == 1) {
                    next = Optional.of(collect2.get(0));
                } else {
                    next = Optional.empty();
                }
            }
        }
        if (next.isPresent()) {
            // /clients/root cannot match /clients/root/**
            if (requestURI.split("/").length != next.get().getPath().split("/").length) {
                return Optional.empty();
            }
        }
        return next;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadAllEndpoints() {
        cached = DomainRegistry.retrieveEndpointService().loadAllEndpoints();
        csrfService.refresh(cached);
        corsService.refresh(cached);
        cacheService.refresh(cached);
    }

    public AntPathMatcher getPathMater() {
        return antPathMatcher;
    }

    public boolean checkAccess(String requestURI, String method, @Nullable String authHeader, boolean webSocket) throws ParseException {
        if (webSocket) {
            if (authHeader == null) {
                log.debug("return 403 due to empty auth info");
                return false;
            }
            if (!DomainRegistry.jwtService().verify(authHeader.replace("Bearer ", ""))) {
                log.debug("return 403 due to jwt failed for verification");
                return false;
            } else {
                //check roles
                return checkAccessByRole(requestURI, method, authHeader, true);
            }
        }
        if (requestURI.contains("/oauth/token") || requestURI.contains("/oauth/token_key")) {
            //permit all token endpoints,
            return true;
        } else if (authHeader == null || !authHeader.contains("Bearer") || requestURI.contains("/public")) {
            if (cached.size() == 0) {
                log.debug("return 403 due to cached endpoints are empty");
                return false;
            }
            List<Endpoint> collect1 = cached.stream().filter(e -> !e.isSecured()).filter(e -> antPathMatcher.match(e.getPath(), requestURI) && method.equals(e.getMethod())).collect(Collectors.toList());
            if (collect1.size() == 0) {
                log.debug("return 403 due to un-registered public endpoints or no authentication info found");
                return false;
            } else {
                return true;
            }
        } else if (authHeader.contains("Bearer")) {
            return checkAccessByRole(requestURI, method, authHeader, false);
        } else {
            log.debug("return 403 due to un-registered endpoints");
            return false;
        }
    }

    private boolean checkAccessByRole(String requestURI, String method, String authHeader, boolean websocket) throws ParseException {
        //check endpoint url, method first then check resourceId and security rule
        String jwtRaw = authHeader.replace("Bearer ", "");
        Set<String> resourceIds = DomainRegistry.jwtService().getResourceIds(jwtRaw);

        //fetch security profile
        if (resourceIds == null || resourceIds.isEmpty()) {
            log.debug("return 403 due to resourceIds is null or empty");
            return false;
        }
        List<Endpoint> collect = cached.stream().filter(e -> resourceIds.contains(e.getResourceId())).collect(Collectors.toList());
        //fetch security rule by endpoint & method
        List<Endpoint> collect1;
        if (websocket) {
            collect1 = collect.stream().filter(e -> antPathMatcher.match(e.getPath(), requestURI) && e.isWebsocket()).collect(Collectors.toList());
        } else {
            collect1 = collect.stream().filter(e -> antPathMatcher.match(e.getPath(), requestURI) && method.equals(e.getMethod())).collect(Collectors.toList());
        }

        Optional<Endpoint> mostSpecificSecurityProfile = getMostSpecificSecurityProfile(collect1, requestURI);
        boolean passed;
        if (mostSpecificSecurityProfile.isPresent()) {
            passed = mostSpecificSecurityProfile.get().allowAccess(jwtRaw);
        } else {
            log.debug("return 403 due to endpoint not found");
            return false;
        }
        if (!passed) {
            log.debug("return 403 due to not pass check");
            return false;
        } else {
            return true;
        }
    }

    public String checkSumValue() {
        //sort before generate check sum
        SortedSet<Endpoint> objects = new TreeSet<>();
        cached.stream().sorted().forEach(objects::add);
        return DomainRegistry.checkSumService().getChecksum(objects);
    }
}
