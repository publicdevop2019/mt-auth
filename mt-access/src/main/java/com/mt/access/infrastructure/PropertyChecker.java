package com.mt.access.infrastructure;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PropertyChecker {
    @Value("${spring.datasource.url}")
    private String datasourceUlr;

    @PostConstruct
    public void checkProperties() {
        if (!datasourceUlr.contains("rewriteBatchedStatements=true")) {
            log.warn(
                "batch insert is disabled, this can greatly impact performance, pls add rewriteBatchedStatements=true to your spring.datasource.url");
        }
        if (datasourceUlr.contains("useSSL=false") ||
            datasourceUlr.contains("allowPublicKeyRetrieval=true")) {
            log.warn(
                "unsafe property detected, considering remove them for security");
        }
        if (datasourceUlr.contains("useSSL=false") ||
            datasourceUlr.contains("allowPublicKeyRetrieval=true")) {
            log.warn(
                "unsafe property detected, remember to remove it in production environment");
        }
    }
}
