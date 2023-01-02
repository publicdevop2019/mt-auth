package com.mt.proxy.domain.response;

import com.google.json.JsonSanitizer;
import com.mt.proxy.infrastructure.filter.ScgCustomFilter;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
@Slf4j
public class SanitizeService {
    public static byte[] sanitizeResp(byte[] responseBody, HttpHeaders headers) {
        if (MediaType.APPLICATION_JSON_UTF8.equals(headers.getContentType())) {
            String responseBodyString =
                new String(responseBody, StandardCharsets.UTF_8);
            String afterSanitize = JsonSanitizer.sanitize(responseBodyString);
            byte[] bytes = afterSanitize.getBytes(StandardCharsets.UTF_8);
            if (headers.getContentLength()
                !=
                afterSanitize.getBytes(StandardCharsets.UTF_8).length) {
                log.debug("sanitized response length before {} after {}",
                    responseBody.length, bytes.length);
                headers.setContentLength(bytes.length);
            }
            return bytes;
        }
        return responseBody;
    }
}
