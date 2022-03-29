package com.mt.proxy.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class GlobalExceptionHandler extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  boolean includeStackTrace) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", 500);
        map.put("message", "please check proxy logs");
        return map;
    }
}
