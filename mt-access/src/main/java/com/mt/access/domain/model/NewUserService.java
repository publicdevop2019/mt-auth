package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class NewUserService {
    public UserId create(UserEmail userEmail, UserPassword password, ActivationCode activationCode,
                         UserId userId) {
        Optional<PendingUser> pendingUser = DomainRegistry.getPendingUserRepository()
            .pendingUserOfEmail(new RegistrationEmail(userEmail.getEmail()));
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        if (pendingUser.isPresent()) {
            if (pendingUser.get().getActivationCode() == null
                ||
                !pendingUser.get().getActivationCode().getActivationCode()
                    .equals(activationCode.getActivationCode())) {
                throw new IllegalArgumentException("activation code mismatch");
            }
            User user = new User(userEmail, password, userId);
            DomainRegistry.getUserRepository().add(user);
            CommonDomainRegistry.getDomainEventRepository()
                .append(new NewUserRegistered(user.getUserId(), userEmail));
            return user.getUserId();
        } else {
            return null;
        }
    }
}
