package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.UserAddEmailCommand;
import com.mt.access.application.user.command.UserAddMobileCommand;
import com.mt.access.application.user.command.UserAddUserNameCommand;
import com.mt.access.application.user.command.UserForgetPasswordCommand;
import com.mt.access.application.user.command.UserResetPasswordCommand;
import com.mt.access.application.user.command.UserUpdateLanguageCommand;
import com.mt.access.application.user.command.UserUpdatePasswordCommand;
import com.mt.access.application.user.representation.UserProfileRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class UserProfileResource {
    private static final String CONTENT_TYPE = "content-type";
    private static final String LOCATION = "Location";

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
     * add username.
     *
     * @param jwt     user jwt
     * @param command add command
     * @param changeId change id
     * @return void
     */
    @PostMapping(path = "users/profile/username")
    public ResponseEntity<Void> addUsername(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody UserAddUserNameCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().addUsername(command, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * delete username.
     *
     * @param jwt user jwt
     * @param changeId change id
     * @return void
     */
    @DeleteMapping(path = "users/profile/username")
    public ResponseEntity<Void> deleteUsername(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().deleteUsername(changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * add mobile.
     *
     * @param jwt     user jwt
     * @param command add command
     * @param changeId change id
     * @return void
     */
    @PostMapping(path = "users/profile/mobile")
    public ResponseEntity<Void> addMobile(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody UserAddMobileCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().addMobile(command, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * delete mobile.
     *
     * @param jwt user jwt
     * @param changeId change id
     * @return void
     */
    @DeleteMapping(path = "users/profile/mobile")
    public ResponseEntity<Void> deleteMobile(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().deleteMobile(changeId);
        return ResponseEntity.ok().build();
    }
    /**
     * add email.
     *
     * @param jwt     user jwt
     * @param command add command
     * @param changeId change id
     * @return void
     */
    @PostMapping(path = "users/profile/email")
    public ResponseEntity<Void> addEmail(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
        @RequestBody UserAddEmailCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().addEmail(command, changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * delete email.
     *
     * @param jwt user jwt
     * @param changeId change id
     * @return void
     */
    @DeleteMapping(path = "users/profile/email")
    public ResponseEntity<Void> deleteEmail(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().deleteEmail(changeId);
        return ResponseEntity.ok().build();
    }

    /**
     * update language.
     *
     * @param jwt user jwt
     * @param command update command
     * @return void
     */
    @PutMapping(path = "users/profile/language")
    public ResponseEntity<Void> updateLanguage(
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestBody UserUpdateLanguageCommand command
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        ApplicationServiceRegistry.getUserApplicationService().updateLanguage(command);
        return ResponseEntity.ok().build();
    }

}
