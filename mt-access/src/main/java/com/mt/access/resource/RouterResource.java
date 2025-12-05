package com.mt.access.resource;

import static com.mt.access.infrastructure.HttpUtility.updateProjectIds;
import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.endpoint.command.EndpointExpireCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.application.endpoint.command.RouterCreateCommand;
import com.mt.access.application.endpoint.command.RouterUpdateCommand;
import com.mt.access.application.endpoint.representation.EndpointCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointMgmtRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProtectedRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProxyCacheRepresentation;
import com.mt.access.application.endpoint.representation.EndpointRepresentation;
import com.mt.access.application.endpoint.representation.EndpointSharedCardRepresentation;
import com.mt.access.application.endpoint.representation.RouterRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class RouterResource {

    @PostMapping(path = "projects/{projectId}/routers")
    public ResponseEntity<Void> tenantCreate(
        @PathVariable String projectId,
        @RequestBody RouterCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        command.setProjectId(projectId);
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getRouterApplicationService()
                .tenantCreate(command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/routers")
    public ResponseEntity<SumPagedRep<?>> tenantQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        SumPagedRep<RouterRepresentation> endpoints =
            ApplicationServiceRegistry.getRouterApplicationService()
                .tenantQuery(projectId, pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping(path = "mgmt/routers")
    public ResponseEntity<SumPagedRep<?>> mgmtQuery(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        SumPagedRep<RouterRepresentation> endpoints =
            ApplicationServiceRegistry.getRouterApplicationService()
                .mgmtQuery(pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping(path = "/routers/proxy")
    public ResponseEntity<SumPagedRep<?>> internalProxyQuery(
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        SumPagedRep<RouterRepresentation> endpoints =
            ApplicationServiceRegistry.getRouterApplicationService()
                .internalProxyQuery(pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @PutMapping("projects/{projectId}/routers/{id}")
    public ResponseEntity<Void> tenantUpdate(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId,
        @RequestBody RouterUpdateCommand command,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getRouterApplicationService()
            .tenantUpdate(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/routers/{id}")
    public ResponseEntity<Void> tenantRemove(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        ApplicationServiceRegistry.getRouterApplicationService()
            .tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

}
