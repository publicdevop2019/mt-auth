package com.mt.proxy.domain;

import com.google.json.JsonSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonSanitizeService {
    public boolean sanitizeRequired(HttpMethod method, MediaType mediaType) {
        return method != null && !method.equals(HttpMethod.GET)
            &&
            !method.equals(HttpMethod.OPTIONS)
            &&
            mediaType != null
            &&
            mediaType.equals(MediaType.APPLICATION_JSON);
    }

    public String sanitizeRequest(String e) {
        String sanitized = JsonSanitizer.sanitize(e);
        if (e.getBytes().length != sanitized.getBytes().length) {
            log.debug("sanitized request length before {} after {}", e.getBytes().length,
                sanitized.getBytes().length);
        }
        return sanitized;
    }

}
