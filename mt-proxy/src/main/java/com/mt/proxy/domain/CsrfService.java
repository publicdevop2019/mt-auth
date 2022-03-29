package com.mt.proxy.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsrfService {
    private final Set<MethodPathKey> bypassList = new HashSet<>();

    public void refresh(Set<Endpoint> endpoints) {
        log.debug("refresh csrf config");
        bypassList.clear();
        endpoints.stream().filter(e -> !e.isCsrfEnabled()).forEach(e -> {
            bypassList.add(new MethodPathKey(e.getMethod(), e.getPath()));
        });
        log.debug("refresh csrf config completed");
    }

    public Set<MethodPathKey> getBypassList() {
        return bypassList;
    }
}
