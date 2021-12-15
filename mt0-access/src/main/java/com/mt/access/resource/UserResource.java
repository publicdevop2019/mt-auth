package com.mt.access.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.*;
import com.mt.access.application.user.representation.UserAdminRepresentation;
import com.mt.access.application.user.representation.UserCardRepresentation;
import com.mt.access.application.user.representation.UserSystemCardRepresentation;
import com.mt.access.domain.model.user.User;
import com.mt.access.infrastructure.JwtAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "users")
public class UserResource {


    @PostMapping
    public ResponseEntity<Void> createForApp(@RequestBody UserCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.getUserApplicationService().create(command, changeId)).build();
    }

    @GetMapping
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                   @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                   @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserApplicationService().users(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserCardRepresentation::new));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserAdminRepresentation> readForAdminById(@PathVariable String id) {
        Optional<User> user = ApplicationServiceRegistry.getUserApplicationService().user(id);
        return user.map(value -> ResponseEntity.ok(new UserAdminRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @PutMapping("{id}")
    public ResponseEntity<Void> updateForAdmin(@RequestBody UpdateUserCommand command,
                                               @PathVariable String id,
                                               @RequestHeader("authorization") String jwt,
                                               @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteForAdminById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().delete(id, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("search")
    public ResponseEntity<SumPagedRep<UserSystemCardRepresentation>> getForAppByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                      @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                      @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<User> users = ApplicationServiceRegistry.getUserApplicationService().users(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep<>(users, UserSystemCardRepresentation::new));
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForAdminById(@PathVariable(name = "id") String id,
                                                  @RequestBody JsonPatch command,
                                                  @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
                                                  @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("pwd")
    public ResponseEntity<Void> updateForUser(@RequestBody UserUpdateBizUserPasswordCommand command,
                                              @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
                                              @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.getUserApplicationService().updatePassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("forgetPwd")
    public ResponseEntity<Void> forgetPwd(@RequestBody UserForgetPasswordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().forgetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("resetPwd")
    public ResponseEntity<Void> resetPwd(@RequestBody UserResetPasswordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.getUserApplicationService().resetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }
}
