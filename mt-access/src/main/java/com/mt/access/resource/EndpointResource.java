package com.mt.access.resource;

import static com.mt.access.infrastructure.HttpUtility.updateProjectIds;
import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.endpoint.command.EndpointCreateCommand;
import com.mt.access.application.endpoint.command.EndpointExpireCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.application.endpoint.representation.EndpointCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointMgmtRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProtectedRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProxyCacheRepresentation;
import com.mt.access.application.endpoint.representation.EndpointRepresentation;
import com.mt.access.application.endpoint.representation.EndpointSharedCardRepresentation;
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
public class EndpointResource {

    @PostMapping(path = "projects/{projectId}/endpoints")
    public ResponseEntity<Void> tenantCreate(
        @PathVariable String projectId,
        @RequestBody EndpointCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        command.setProjectId(projectId);
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getEndpointApplicationService()
                .tenantCreate(command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/endpoints")
    public ResponseEntity<SumPagedRep<?>> tenantQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        queryParam = updateProjectIds(queryParam, projectId);
        SumPagedRep<EndpointCardRepresentation> endpoints =
            ApplicationServiceRegistry.getEndpointApplicationService()
                .tenantQuery(queryParam, pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping(path = "mgmt/endpoints")
    public ResponseEntity<SumPagedRep<EndpointCardRepresentation>> mgmtQuery(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        SumPagedRep<EndpointCardRepresentation> endpoints =
            ApplicationServiceRegistry.getEndpointApplicationService()
                .mgmtQuery(queryParam, pageParam, config);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping(path = "mgmt/endpoints/{id}")
    public ResponseEntity<EndpointMgmtRepresentation> mgmtGet(
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        EndpointMgmtRepresentation endpoint =
            ApplicationServiceRegistry.getEndpointApplicationService().mgmtQueryById(id);
        return ResponseEntity.ok(endpoint);
    }

    /**
     * get paginated endpoints for proxy to cache
     *
     * @param pageParam pagination info
     * @return paginated data
     */
    @GetMapping("endpoints/proxy")
    public ResponseEntity<SumPagedRep<?>> proxyQuery(
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam
    ) {
        SumPagedRep<EndpointProxyCacheRepresentation> endpoints =
            ApplicationServiceRegistry.getEndpointApplicationService()
                .proxyQuery(pageParam);
        return ResponseEntity.ok(endpoints);
    }

    @GetMapping("projects/{projectId}/endpoints/{id}")
    public ResponseEntity<EndpointRepresentation> tenantGet(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId,
        @PathVariable String id
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        Endpoint endpoint = ApplicationServiceRegistry.getEndpointApplicationService()
            .tenantQueryById(projectId, id);
        return ResponseEntity.ok(new EndpointRepresentation(endpoint));
    }

    @PutMapping("projects/{projectId}/endpoints/{id}")
    public ResponseEntity<Void> tenantUpdate(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId,
        @RequestBody EndpointUpdateCommand command,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getEndpointApplicationService()
            .tenantUpdate(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/endpoints/{id}")
    public ResponseEntity<Void> tenantRemove(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        ApplicationServiceRegistry.getEndpointApplicationService()
            .tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("projects/{projectId}/endpoints/{id}/expire")
    public ResponseEntity<Void> tenantExpire(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody EndpointExpireCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        ApplicationServiceRegistry.getEndpointApplicationService()
            .expire(command, projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "mgmt/endpoints/event/reload")
    public ResponseEntity<Void> mgmtReload(
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        ApplicationServiceRegistry.getEndpointApplicationService().reloadCache(changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "endpoints/shared")
    public ResponseEntity<SumPagedRep<EndpointSharedCardRepresentation>> marketQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        SumPagedRep<EndpointSharedCardRepresentation> shared =
            ApplicationServiceRegistry.getEndpointApplicationService()
                .marketQuery(queryParam, pageParam, config);
        return ResponseEntity.ok(shared);
    }

    @GetMapping(path = "projects/{projectId}/endpoints/protected")
    public ResponseEntity<SumPagedRep<EndpointProtectedRepresentation>> tenantRoleQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        queryParam = updateProjectIds(queryParam, projectId);
        SumPagedRep<EndpointProtectedRepresentation> protectedEp =
            ApplicationServiceRegistry.getEndpointApplicationService()
                .tenantQueryProtected(queryParam, pageParam, config);
        return ResponseEntity.ok(protectedEp);
    }
}
