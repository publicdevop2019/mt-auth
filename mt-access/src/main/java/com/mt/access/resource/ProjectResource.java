package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.project.command.ProjectCreateCommand;
import com.mt.access.application.project.command.ProjectUpdateCommand;
import com.mt.access.application.project.representation.ProjectCardRepresentation;
import com.mt.access.application.project.representation.ProjectRepresentation;
import com.mt.access.domain.model.project.Project;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
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
public class ProjectResource {

    @PostMapping(path = "projects")
    public ResponseEntity<Void> createForRoot(@RequestBody ProjectCreateCommand command,
                                              @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
                                              @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                  String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        return ResponseEntity.ok().header("Location",
            ApplicationServiceRegistry.getProjectApplicationService().create(command, changeId))
            .build();
    }

    @GetMapping(path = "mngmt/projects")
    public ResponseEntity<SumPagedRep<ProjectCardRepresentation>> readForRootByQuery(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String skipCount,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        SumPagedRep<Project> clients = ApplicationServiceRegistry.getProjectApplicationService()
            .adminQueryProjects(queryParam, pageParam, skipCount);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ProjectCardRepresentation::new));
    }

    @GetMapping(path = "projects/tenant")
    public ResponseEntity<SumPagedRep<ProjectCardRepresentation>> externalQuery(
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,

        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        SumPagedRep<Project> clients =
            ApplicationServiceRegistry.getProjectApplicationService().findTenantProjects(pageParam);
        return ResponseEntity.ok(new SumPagedRep<>(clients, ProjectCardRepresentation::new));
    }

    @GetMapping("projects/{id}")
    public ResponseEntity<ProjectRepresentation> readForRootById(
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        Optional<Project> client =
            ApplicationServiceRegistry.getProjectApplicationService().project(id);
        return client.map(value -> ResponseEntity.ok(new ProjectRepresentation(value)))
            .orElseGet(() -> ResponseEntity.ok().build());
    }


    @PutMapping("projects/{id}")
    public ResponseEntity<Void> replaceForRootById(@PathVariable(name = "id") String id,
                                                   @RequestBody ProjectUpdateCommand command,
                                                   @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                       String changeId,
                                                   @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                       String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getProjectApplicationService().replace(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("projects/{id}")
    public ResponseEntity<Void> deleteForRootById(@PathVariable String id,
                                                  @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                      String changeId,
                                                  @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                      String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getProjectApplicationService().removeProject(id, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "projects/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForRootById(@PathVariable(name = "id") String id,
                                                 @RequestBody JsonPatch command,
                                                 @RequestHeader(HTTP_HEADER_CHANGE_ID)
                                                     String changeId,
                                                 @RequestHeader(HTTP_HEADER_AUTHORIZATION)
                                                     String jwt) {
        JwtCurrentUserService.JwtThreadLocal.unset();
        JwtCurrentUserService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getProjectApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }
}
