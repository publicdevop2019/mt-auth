package com.mt.access.resource;

import static com.mt.access.infrastructure.Utility.updateProjectIds;
import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.command.ClientCreateCommand;
import com.mt.access.application.client.command.ClientUpdateCommand;
import com.mt.access.application.client.representation.ClientAutoApproveRepresentation;
import com.mt.access.application.client.representation.ClientCardRepresentation;
import com.mt.access.application.client.representation.ClientDropdownRepresentation;
import com.mt.access.application.client.representation.ClientProxyRepresentation;
import com.mt.access.application.client.representation.ClientRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.infrastructure.HttpUtility;
import com.mt.common.domain.model.restful.SumPagedRep;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class ClientResource {

    @PostMapping(path = "projects/{projectId}/clients")
    public ResponseEntity<Void> tenantCreate(
        @PathVariable String projectId,
        @RequestBody ClientCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        HttpServletRequest servletRequest
    ) {
        String clientIpAddress = HttpUtility.getClientIpAddress(servletRequest);
        log.info("tenant ip {}", clientIpAddress);
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getClientApplicationService()
                .tenantCreate(command, changeId)).build();
    }

    @GetMapping(path = "projects/{projectId}/clients")
    public ResponseEntity<SumPagedRep<ClientCardRepresentation>> tenantQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = updateProjectIds(queryParam, projectId);
        SumPagedRep<ClientCardRepresentation> rep = ApplicationServiceRegistry.getClientApplicationService()
            .tenantQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(rep);
    }

    @GetMapping(path = "projects/{projectId}/clients/dropdown")
    public ResponseEntity<SumPagedRep<ClientDropdownRepresentation>> tenantDropdownQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @PathVariable String projectId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = updateProjectIds(queryParam, projectId);
        SumPagedRep<ClientDropdownRepresentation> clients = ApplicationServiceRegistry.getClientApplicationService()
            .tenantDropdownQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(clients);
    }

    @GetMapping(path = "mgmt/clients")
    public ResponseEntity<SumPagedRep<?>> mgmtQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<ClientCardRepresentation> rep = ApplicationServiceRegistry.getClientApplicationService()
            .mgmtQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(rep);
    }

    @GetMapping(path = "mgmt/clients/dropdown")
    public ResponseEntity<SumPagedRep<?>> mgmtDropdownQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<ClientDropdownRepresentation> rep = ApplicationServiceRegistry.getClientApplicationService()
            .mgmtDropdownQuery(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(rep);
    }

    @GetMapping("mgmt/clients/{id}")
    public ResponseEntity<ClientRepresentation> mgmtGet(
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        Client client =
            ApplicationServiceRegistry.getClientApplicationService().mgmtQueryById(id);
        return ResponseEntity.ok(new ClientRepresentation(client));
    }

    //for internal proxy to create router
    @GetMapping(path = "clients/proxy")
    public ResponseEntity<SumPagedRep<ClientProxyRepresentation>> proxyQuery(
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount
    ) {
        SumPagedRep<Client> clients = ApplicationServiceRegistry.getClientApplicationService()
            .proxyQuery(pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ClientProxyRepresentation::new));
    }

    @GetMapping("projects/{projectId}/clients/{id}")
    public ResponseEntity<ClientRepresentation> tenantGet(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        Client client =
            ApplicationServiceRegistry.getClientApplicationService().tenantQueryById(id, projectId);
        return ResponseEntity.ok(new ClientRepresentation(client));
    }

    @PutMapping("projects/{projectId}/clients/{id}")
    public ResponseEntity<Void> tenantUpdate(
        @PathVariable String projectId,
        @PathVariable(name = "id") String id,
        @RequestBody ClientUpdateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        command.setProjectId(projectId);
        ApplicationServiceRegistry.getClientApplicationService()
            .tenantUpdate(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{projectId}/clients/{id}")
    public ResponseEntity<Void> tenantRemove(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getClientApplicationService()
            .tenantRemove(projectId, id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{projectId}/clients/{id}",
        consumes = "application/json-patch+json")
    public ResponseEntity<Void> tenantPatch(
        @PathVariable String projectId,
        @PathVariable(name = "id") String id,
        @RequestBody JsonPatch command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getClientApplicationService()
            .tenantPatch(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("projects/{projectId}/clients/{id}/autoApprove")
    public ResponseEntity<ClientAutoApproveRepresentation> uiAutoApproveCheck(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        Client client =
            ApplicationServiceRegistry.getClientApplicationService().canAutoApprove(projectId, id);
        return ResponseEntity.ok(new ClientAutoApproveRepresentation(client));
    }

}
