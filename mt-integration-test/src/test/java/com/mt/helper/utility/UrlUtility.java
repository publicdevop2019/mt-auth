package com.mt.helper.utility;

import com.mt.helper.AppConstant;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

@Slf4j
public class UrlUtility {

    public static String getId(ResponseEntity<?> entity) {
        log.info("get id from response body");
        if (entity.getHeaders().getLocation() == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info("id not found, response code {} body {}", entity.getStatusCode(),
                    objectMapper.writeValueAsString(entity.getBody()));
            } catch (IOException e) {
                log.info("error reading response body");
                throw new RuntimeException(e);
            }
        }
        Assertions.assertNotNull(entity.getHeaders().getLocation());
        return Objects.requireNonNull(entity.getHeaders().getLocation()).toString();
    }

    public static String getAccessUrl(String path) {
        String normalized = removeLeadingSlash(path);
        return AppConstant.PROXY_URL + "/auth-svc/" + normalized;
    }

    public static String getTenantUrl(String clientPath, String path) {
        String normalized = removeLeadingSlash(path);
        String normalized2 = removeLeadingSlash(clientPath);
        return AppConstant.PROXY_URL + "/" + normalized2 + "/" + normalized;
    }

    public static String getPageQuery(int pageNum, int size) {
        return "page=num:" + pageNum + ",size:" + size;
    }

    public static String appendPath(String url, String path) {
        String normalized = removeLeadingSlash(path);
        return url + "/" + normalized;
    }

    public static String appendQuery(String url, String query) {
        String normalized = removeLeadingQuestionMark(query);
        return url + "?" + normalized;
    }

    public static String combinePath(String path1, String path2) {
        String normalized1 = removeLeadingSlash(path1);
        String normalized2 = removeLeadingSlash(path2);
        return normalized1 + "/" + normalized2;
    }

    public static String combinePath(String path1, String path2, String path3) {
        String normalized1 = removeLeadingSlash(path1);
        String normalized2 = removeLeadingSlash(path2);
        String normalized3 = removeLeadingSlash(path3);
        return normalized1 + "/" + normalized2 + "/" + normalized3;
    }

    public static String getTestUrl(String path) {
        String normalized = removeLeadingSlash(path);
        return AppConstant.PROXY_URL + "/test-svc/" + normalized;
    }

    private static String removeLeadingSlash(String path) {
        return path.replaceAll("^/+", "");
    }

    private static String removeLeadingQuestionMark(String path) {
        return path.replaceAll("^/?+", "");
    }
}
