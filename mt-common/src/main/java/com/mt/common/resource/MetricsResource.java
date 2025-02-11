package com.mt.common.resource;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MetricsResource {
    @Autowired
    private PrometheusMeterRegistry meterRegistry;

    @GetMapping(path = "metrics", produces = "text/plain")
    public ResponseEntity<String> metrics() {
        return ResponseEntity.ok(meterRegistry.scrape());
    }
}
