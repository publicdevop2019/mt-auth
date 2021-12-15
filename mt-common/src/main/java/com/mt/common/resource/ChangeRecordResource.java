package com.mt.common.resource;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.idempotent.ChangeRecordApplicationService;
import com.mt.common.application.idempotent.representation.ChangeRecordRepresentation;
import com.mt.common.domain.model.idempotent.ChangeRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "changes")
public class ChangeRecordResource {

    @GetMapping("root")
    public ResponseEntity<?> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        return ResponseEntity.ok(changeRecordApplicationService().changeRecords(queryParam, pageParam, skipCount));
    }

    private ChangeRecordApplicationService changeRecordApplicationService() {
        return CommonApplicationServiceRegistry.getChangeRecordApplicationService();
    }

}
