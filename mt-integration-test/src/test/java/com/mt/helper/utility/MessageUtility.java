package com.mt.helper.utility;

import com.mt.helper.pojo.Notification;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class MessageUtility {

    private static final ParameterizedTypeReference<SumTotal<Notification>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl() {
        return HttpUtility.getAccessUrl("user/notifications/bell");
    }

    public static ResponseEntity<SumTotal<Notification>> readMessages(User user) {
        return Utility.readResource(user, getUrl(), reference);
    }
}
