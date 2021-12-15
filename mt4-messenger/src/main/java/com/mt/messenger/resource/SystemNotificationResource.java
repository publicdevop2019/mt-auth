package com.mt.messenger.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.application.system_notification.SystemNotificationRepresentation;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "systemNotifications")
public class SystemNotificationResource {
    @GetMapping("root")
    public ResponseEntity<SumPagedRep<SystemNotificationRepresentation>> getNotifications(@RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                          @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<SystemNotification> notificationsOf=ApplicationServiceRegistry.getSystemNotificationApplicationService().notificationsOf(pageParam,skipCount);
        return ResponseEntity.ok(new SumPagedRep(notificationsOf,SystemNotificationRepresentation::new));
    }
}
