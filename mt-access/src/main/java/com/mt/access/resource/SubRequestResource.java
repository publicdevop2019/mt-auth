package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.sub_request.command.CreateSubRequestCommand;
import com.mt.access.application.sub_request.command.RejectSubRequestCommand;
import com.mt.access.application.sub_request.command.UpdateSubRequestCommand;
import com.mt.access.application.sub_request.representation.SubRequestRepresentation;
import com.mt.access.application.sub_request.representation.SubscriptionRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class SubRequestResource {
    /**
     * get all subscription requests
     *
     * @param jwt       user jwt
     * @param pageParam page size and page number
     * @return paginated subscription request
     */
    @GetMapping(path = "subscriptions/requests")
    public ResponseEntity<SumPagedRep<SubRequestRepresentation>> getMySubscriptionRequests(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<SubRequest> result =
            ApplicationServiceRegistry.getSubRequestApplicationService()
                .query(queryParam, pageParam);
        SumPagedRep<SubRequestRepresentation> resp =
            new SumPagedRep<>(result, SubRequestRepresentation::new);
        SubRequestRepresentation.updateEndpointNames(resp);
        SubRequestRepresentation.updateProjectNames(resp);
        return ResponseEntity.ok(resp);
    }

    /**
     * get all subscriptions regardless of project for user's project
     *
     * @param jwt       user jwt
     * @param pageParam page size and page number
     * @return paginated subscription request
     */
    @GetMapping(path = "subscriptions")
    public ResponseEntity<SumPagedRep<SubscriptionRepresentation>> getMySubscription(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<SubRequest> result =
            ApplicationServiceRegistry.getSubRequestApplicationService()
                .subscriptions(pageParam);
        SumPagedRep<SubscriptionRepresentation> resp =
            new SumPagedRep<>(result, SubscriptionRepresentation::new);
        SubscriptionRepresentation.updateProjectNames(resp);
        SubscriptionRepresentation.updateEndpointDetails(resp);
        return ResponseEntity.ok(resp);
    }

    /**
     * create subscription requests
     *
     * @param jwt      user jwt
     * @param command  create command
     * @param changeId unique change id
     * @return void
     */
    @PostMapping(path = "subscriptions/requests")
    public ResponseEntity<Void> create(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody CreateSubRequestCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        return ResponseEntity.ok().header("Location",
                ApplicationServiceRegistry.getSubRequestApplicationService()
                    .create(command, changeId))
            .build();
    }

    /**
     * update subscription requests
     *
     * @param jwt      user jwt
     * @param id       id
     * @param command  update command
     * @param changeId unique change id
     * @return void
     */
    @PutMapping(path = "subscriptions/requests/{id}")
    public ResponseEntity<Void> update(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String id,
        @RequestBody UpdateSubRequestCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getSubRequestApplicationService()
            .update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * cancel subscription requests
     *
     * @param jwt      user jwt
     * @param id       id
     * @param changeId unique change id
     * @return void
     */
    @PostMapping(path = "subscriptions/requests/{id}/cancel")
    public ResponseEntity<Void> cancel(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String id
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getSubRequestApplicationService()
            .cancel(id, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * approve subscription requests
     *
     * @param jwt      user jwt
     * @param id       id
     * @param changeId unique change id
     * @return void
     */
    @PostMapping(path = "subscriptions/requests/{id}/approve")
    public ResponseEntity<Void> approve(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String id
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getSubRequestApplicationService()
            .approve(id, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * reject subscription requests
     *
     * @param jwt      user jwt
     * @param id       id
     * @param changeId unique change id
     * @return void
     */
    @PostMapping(path = "subscriptions/requests/{id}/reject")
    public ResponseEntity<Void> reject(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String id,
        @RequestBody RejectSubRequestCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getSubRequestApplicationService()
            .reject(id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
