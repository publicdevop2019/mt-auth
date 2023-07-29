package com.mt.common.resource;

import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.idempotent.ChangeRecordApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "changes")
public class ChangeRecordResource {
    //TODO remove or add to UI
    @GetMapping("root")
    public ResponseEntity<?> readForRootByQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity
            .ok(changeRecordApplicationService().changeRecords(queryParam, pageParam, skipCount));
    }

    private ChangeRecordApplicationService changeRecordApplicationService() {
        return CommonApplicationServiceRegistry.getChangeRecordApplicationService();
    }

}
