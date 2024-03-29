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
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class NewUserService {
    public UserId create(UserEmail email,
                         UserPassword password,
                         ActivationCode activationCode,
                         UserMobile mobile,
                         UserId userId, TransactionContext context) {
        Optional<PendingUser> pendingUser = DomainRegistry.getPendingUserRepository()
            .query(new RegistrationEmail(email.getEmail()));
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        if (pendingUser.isPresent()) {
            if (pendingUser.get().getActivationCode() == null
                ||
                !pendingUser.get().getActivationCode().getActivationCode()
                    .equals(activationCode.getActivationCode())) {
                throw new DefinedRuntimeException("activation code mismatch", "1025",
                    HttpResponseCode.BAD_REQUEST);
            }
            User user = User.newUser(email, password, userId, mobile);
            DomainRegistry.getUserRepository().add(user);
            context
                .append(new NewUserRegistered(user.getUserId(), email));
            return user.getUserId();
        } else {
            throw new DefinedRuntimeException("pending user not found, maybe not registered?",
                "1026",
                HttpResponseCode.BAD_REQUEST);
        }
    }
}
