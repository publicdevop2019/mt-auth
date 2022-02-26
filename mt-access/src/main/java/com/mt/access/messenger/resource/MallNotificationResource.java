package com.mt.access.messenger.resource;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.application.mall_notification.MallNotificationRepresentation;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
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
@RequestMapping(produces = "application/json", path = "mallNotifications")
public class MallNotificationResource {
    @GetMapping("admin")
    public ResponseEntity<SumPagedRep<MallNotificationRepresentation>> getNotifications(@RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<MallNotification> mallNotificationSumPagedRep = ApplicationServiceRegistry.getMallNotificationApplicationService().notificationsOf(pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep(mallNotificationSumPagedRep, MallNotificationRepresentation::new));
    }
}
