package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Notification;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class MessageUtility {

    private static final ParameterizedTypeReference<SumTotal<Notification>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl() {
        return UrlUtility.getAccessUrl("user/notifications/bell");
    }

    public static ResponseEntity<SumTotal<Notification>> readMessages(User user) {
        return Utility.readResource(user, getUrl(), reference);
    }
}
