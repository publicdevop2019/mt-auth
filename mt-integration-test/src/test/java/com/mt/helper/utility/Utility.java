package com.mt.helper.utility;

import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

@Slf4j
public class Utility {
    public static <T> ResponseEntity<SumTotal<T>> readResource(User user, String url,
                                                               ParameterizedTypeReference<SumTotal<T>> param) {
        String login = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request,
            param);
    }

    public static <T> ResponseEntity<T> readResource(User user, String url,
                                                     String pathSuffix, Class<T> tClass) {
        return readResource(user, HttpUtility.appendPath(url, pathSuffix), tClass);
    }

    public static <T> ResponseEntity<T> readResource(User user, String url, Class<T> tClass) {
        String login = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, tClass);
    }

    public static <T> ResponseEntity<Void> createResource(User user, String url,
                                                          @Nullable T resource) {
        String login = UserUtility.login(user);
        log.info("login token {}", login);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        if (resource == null) {
            HttpEntity<Void> request =
                new HttpEntity<>(headers);
            return TestContext.getRestTemplate().exchange(url,
                HttpMethod.POST, request,
                Void.class);
        } else {
            HttpEntity<T> request =
                new HttpEntity<>(resource, headers);
            return TestContext.getRestTemplate().exchange(url,
                HttpMethod.POST, request,
                Void.class);
        }
    }

    public static <T> ResponseEntity<Void> createResource(String token, String url,
                                                          @Nullable T resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        if (resource == null) {
            HttpEntity<Void> request =
                new HttpEntity<>(headers);
            return TestContext.getRestTemplate().exchange(url,
                HttpMethod.POST, request,
                Void.class);
        } else {
            HttpEntity<T> request =
                new HttpEntity<>(resource, headers);
            return TestContext.getRestTemplate().exchange(url,
                HttpMethod.POST, request,
                Void.class);
        }
    }

    public static <T> ResponseEntity<Void> updateResource(User user, String url,
                                                          T resource, String resourceId) {
        String login = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<T> request =
            new HttpEntity<>(resource, headers);
        return TestContext.getRestTemplate().exchange(
            HttpUtility.appendPath(url, resourceId),
            HttpMethod.PUT, request,
            Void.class);
    }

    public static <T> ResponseEntity<Void> createResource(User user, String url) {
        return createResource(user, url, null);
    }

    public static ResponseEntity<Void> deleteResource(User user, String url, String resourceId) {
        String login = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Void> request =
            new HttpEntity<>(headers);
        return TestContext.getRestTemplate().exchange(
            HttpUtility.appendPath(url, resourceId),
            HttpMethod.DELETE, request,
            Void.class);
    }

    public static ResponseEntity<Void> patchResource(User user, String url,
                                                     PatchCommand command, String resourceId) {
        String login = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        headers.set("Content-Type", "application/json-patch+json");
        HttpEntity<List<PatchCommand>> request =
            new HttpEntity<>(Lists.list(command), headers);
        return TestContext.getRestTemplate().exchange(HttpUtility.appendPath(url, resourceId),
            HttpMethod.PATCH, request,
            Void.class);
    }

}
