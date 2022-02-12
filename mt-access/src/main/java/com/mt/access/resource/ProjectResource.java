package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.project.command.ProjectCreateCommand;
import com.mt.access.application.project.command.ProjectUpdateCommand;
import com.mt.access.application.project.representation.ProjectCardRepresentation;
import com.mt.access.application.project.representation.ProjectRepresentation;
import com.mt.access.application.user.representation.UserCardRepresentation;
import com.mt.access.application.user_relation.UpdateUserRelationCommand;
import com.mt.access.application.user_relation.representation.UserTenantRepresentation;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.access.infrastructure.JwtAuthenticationService;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@Slf4j
@RestController
@RequestMapping(produces = "application/json", path = "projects")
public class ProjectResource {

    @PostMapping
    public ResponseEntity<Void> createForRoot(@RequestBody ProjectCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getProjectApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<ProjectCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                     @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                     @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
                                                                                     @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        SumPagedRep<Project> clients = ApplicationServiceRegistry.getProjectApplicationService().projects(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ProjectCardRepresentation::new));
    }

    @GetMapping(path = "tenant")
    public ResponseEntity<SumPagedRep<ProjectCardRepresentation>> externalQuery(
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,

            @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        SumPagedRep<Project> clients = ApplicationServiceRegistry.getProjectApplicationService().findTenantProjects(pageParam);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ProjectCardRepresentation::new));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectRepresentation> readForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        Optional<Project> client = ApplicationServiceRegistry.getProjectApplicationService().project(id);
        return client.map(value -> ResponseEntity.ok(new ProjectRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping(path = "{id}/users")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> findUserForProject(
            @PathVariable String id,
            @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
            @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
            @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserRelationApplicationService().users(id,queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }
    @GetMapping(path = "{id}/users/{userId}")
    public ResponseEntity<UserTenantRepresentation> findUserDetailForProject(
            @PathVariable String id,
            @PathVariable String userId
    ) {
        Optional<UserRelation> userRelationDetail = ApplicationServiceRegistry.getUserRelationApplicationService().getUserRelationDetail(id, userId);
        Optional<User> user = ApplicationServiceRegistry.getUserApplicationService().user(userId);
        return userRelationDetail.map(value -> ResponseEntity.ok(new UserTenantRepresentation(value,user.get()))).orElseGet(() -> ResponseEntity.ok().build());
    }

    @PutMapping(path = "{id}/users/{userId}")
    public ResponseEntity<UserTenantRepresentation> replaceUserDetailForProject(
            @PathVariable String id,
            @PathVariable String userId,
            @RequestBody UpdateUserRelationCommand command
    ) {
        ApplicationServiceRegistry.getUserRelationApplicationService().adminUpdate(id, userId,command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") String id, @RequestBody ProjectUpdateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getProjectApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getProjectApplicationService().removeProject(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id, @RequestBody JsonPatch command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getProjectApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
