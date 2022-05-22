package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.notification.representation.NotificationRepresentation;
import com.mt.access.domain.model.notification.Notification;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class NotificationResource {
    @GetMapping(path = "mngmt/notifications")
    public ResponseEntity<SumPagedRep<NotificationRepresentation>> getNotifications(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<Notification> notificationsOf =
            ApplicationServiceRegistry.getNotificationApplicationService()
                .notificationsOf(queryParam, pageParam, skipCount);
        return ResponseEntity
            .ok(new SumPagedRep<>(notificationsOf, NotificationRepresentation::new));
    }

    @PostMapping(path = "mngmt/notifications/{id}/ack")
    public ResponseEntity<SumPagedRep<Void>> ackNotifications(
        @PathVariable(name = "id") String id) {
        ApplicationServiceRegistry.getNotificationApplicationService()
            .acknowledge(id);
        return ResponseEntity
            .ok().build();
    }
}
