package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Notification;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
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
