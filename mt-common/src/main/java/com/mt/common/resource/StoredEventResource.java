package com.mt.common.resource;

import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.domain_event.StoredEventRepresentation;
import com.mt.common.domain.model.domain_event.StoredEvent;
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

public class StoredEventResource {
    /**
     * retry a stored event
     * @param id stored event id
     * @return void
     */
    @PostMapping("mngmt/events/{id}/retry")
    public ResponseEntity<?> publish(@PathVariable(name = "id") long id) {
        CommonApplicationServiceRegistry.getStoredEventApplicationService().retry(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("mngmt/events")
    public ResponseEntity<?> query(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount) {
        SumPagedRep<StoredEvent> query =
            CommonApplicationServiceRegistry.getStoredEventApplicationService()
                .query(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(query, StoredEventRepresentation::new));
    }

}
