package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;
import static com.mt.common.CommonConstant.HTTP_PARAM_PAGE;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;
import static com.mt.common.CommonConstant.HTTP_PARAM_SKIP_COUNT;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.UpdateUserCommand;
import com.mt.access.application.user.command.UpdateUserRelationCommand;
import com.mt.access.application.user.command.UserCreateCommand;
import com.mt.access.application.user.command.UserForgetPasswordCommand;
import com.mt.access.application.user.command.UserResetPasswordCommand;
import com.mt.access.application.user.command.UserUpdatePasswordCommand;
import com.mt.access.application.user.command.UserUpdateProfileCommand;
import com.mt.access.application.user.representation.ProjectAdminRepresentation;
import com.mt.access.application.user.representation.UserCardRepresentation;
import com.mt.access.application.user.representation.UserMgmtRepresentation;
import com.mt.access.application.user.representation.UserProfileRepresentation;
import com.mt.access.application.user.representation.UserTenantRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.user.User;
import com.mt.access.infrastructure.Utility;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class UserResource {
    private static final String CONTENT_TYPE = "content-type";
    private static final String LOCATION = "Location";

    /**
     * register new user.
     *
     * @param command  register command
     * @param changeId changeId
     * @return void
     */
    @PostMapping(path = "users")
    public ResponseEntity<Void> create(
        @RequestBody UserCreateCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        return ResponseEntity.ok().header("Location",
                ApplicationServiceRegistry.getUserApplicationService().create(command, changeId))
            .build();
    }

    @GetMapping(path = "mgmt/users")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> query(
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserApplicationService()
            .query(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }


    @GetMapping("mgmt/users/{id}")
    public ResponseEntity<UserMgmtRepresentation> mgmtGet(
        @PathVariable String id
    ) {
        UserMgmtRepresentation detail =
            ApplicationServiceRegistry.getUserApplicationService().mgmtQuery(id);
        return ResponseEntity.ok(detail);
    }


    @PutMapping("mgmt/users/{id}")
    public ResponseEntity<Void> mgmtLock(
        @RequestBody UpdateUserCommand command,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().mgmtLock(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "users/pwd")
    public ResponseEntity<Void> updatePassword(
        @RequestBody UserUpdatePasswordCommand command,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().updatePassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * send forget pwd email to user email on system.
     *
     * @param command  forget pwd command
     * @param changeId change id
     * @return void
     */
    @PostMapping(path = "users/forgetPwd")
    public ResponseEntity<Void> forgetPwd(
        @RequestBody UserForgetPasswordCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        ApplicationServiceRegistry.getUserApplicationService().forgetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "users/resetPwd")
    public ResponseEntity<Void> resetPwd(
        @RequestBody UserResetPasswordCommand command,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        ApplicationServiceRegistry.getUserApplicationService().resetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "projects/{projectId}/users")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> tenantQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
        @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        queryParam = Utility.updateProjectIds(queryParam, projectId);
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserRelationApplicationService()
            .tenantUsers(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }

    @GetMapping(path = "projects/{projectId}/users/{id}")
    public ResponseEntity<UserTenantRepresentation> tenantGet(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        UserTenantRepresentation user =
            ApplicationServiceRegistry.getUserRelationApplicationService()
                .tenantUser(projectId, id);
        return ResponseEntity.ok(user);
    }

    /**
     * read my profile.
     *
     * @param jwt user jwt
     * @return user profile
     */
    @GetMapping(path = "users/profile")
    public ResponseEntity<UserProfileRepresentation> myProfile(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        UserProfileRepresentation user =
            ApplicationServiceRegistry.getUserApplicationService().myProfile();
        return ResponseEntity.ok(user);
    }

    /**
     * get my profile avatar.
     *
     * @param jwt user jwt
     * @return binary
     */
    @GetMapping(path = "users/profile/avatar")
    public ResponseEntity<byte[]> profileAvatar(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        Optional<Image> avatar =
            ApplicationServiceRegistry.getUserApplicationService().queryProfileAvatar();
        return avatar.map(e -> {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(CONTENT_TYPE,
                e.getContentType());
            responseHeaders.setContentDispositionFormData(e.getOriginalName(), e.getOriginalName());
            return ResponseEntity.ok().headers(responseHeaders).body(e.getSource());
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * update or create my profile avatar.
     *
     * @param jwt user jwt
     * @return void
     */
    @PostMapping(path = "users/profile/avatar")
    public ResponseEntity<Void> createProfileAvatar(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam("file") MultipartFile file,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ImageId imageId = ApplicationServiceRegistry.getUserApplicationService()
            .createProfileAvatar(file, changeId);
        return ResponseEntity.ok().header(LOCATION, imageId.getDomainId()).build();
    }

    /**
     * update my profile.
     *
     * @param jwt     user jwt
     * @param command update command
     * @return void
     */
    @PutMapping(path = "users/profile")
    public ResponseEntity<Void> updateProfile(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestBody UserUpdateProfileCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().updateProfile(command);
        return ResponseEntity.ok().build();
    }

    /**
     * update user role for project.
     *
     * @param projectId project id
     * @param id        user id
     * @param jwt       jwt
     * @param command   update command
     * @return http response 200
     */
    @PutMapping(path = "projects/{projectId}/users/{id}")
    public ResponseEntity<Void> tenantUpdate(
        @PathVariable String projectId,
        @PathVariable String id,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody UpdateUserRelationCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .tenantUpdate(projectId, id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "projects/{projectId}/admins")
    public ResponseEntity<SumPagedRep<ProjectAdminRepresentation>> adminQuery(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        SumPagedRep<ProjectAdminRepresentation> resp =
            ApplicationServiceRegistry.getUserRelationApplicationService()
                .adminQuery(pageParam, projectId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(path = "projects/{projectId}/admins/{userId}")
    public ResponseEntity<Void> addAdmin(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String userId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .addAdmin(projectId, userId, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "projects/{projectId}/admins/{userId}")
    public ResponseEntity<Void> removeAdmin(
        @PathVariable String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @PathVariable String userId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserRelationApplicationService()
            .removeAdmin(projectId, userId, changeId);
        return ResponseEntity.ok().build();
    }
}
