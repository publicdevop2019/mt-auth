package com.hw.helper.utility;

import com.hw.helper.Notification;
import com.hw.helper.SumTotal;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class NotificationUtility {

    public static ResponseEntity<SumTotal<Notification>> getUserNotification(String jwt) {
        String url = UrlUtility.getAccessUrl("user/notifications/bell");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(jwt);
        headers1.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String>
            hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }
}
