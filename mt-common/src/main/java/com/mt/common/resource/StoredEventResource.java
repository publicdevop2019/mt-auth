package com.mt.common.resource;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.domain_event.StoredEventRepresentation;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "events")

public class StoredEventResource {
    @PostMapping("admin/{id}/retry")
    public ResponseEntity<?> publish(@PathVariable(name = "id") long id) {
        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("admin")
    public ResponseEntity<?> query(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                   @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                   @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<StoredEvent> query = CommonApplicationServiceRegistry.getStoredEventApplicationService().query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(query, StoredEventRepresentation::new));
    }

}
