package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;

import com.hw.helper.Cors;
import com.hw.helper.Notification;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class MessageUtility {



    public static ResponseEntity<SumTotal<Notification>> readMessages(User user) {
        String login =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl("user/notifications/bell"),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }
}
