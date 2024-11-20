package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserName;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.verification_code.VerificationCode;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import org.springframework.stereotype.Service;

@Service
public class NewUserService {

    public UserId create(UserMobile userMobile, VerificationCode code, UserId userId,
                         TransactionContext context) {
        DomainRegistry.getTemporaryCodeService()
            .verifyCode(code.getValue(), VerificationCode.EXPIRE_AFTER_MILLI,
                VerificationCode.OPERATION_TYPE,
                userMobile.getValue());
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        User user = User.newUser(userMobile, userId);
        DomainRegistry.getUserRepository().add(user);
        context
            .append(new NewUserRegistered(user.getUserId(), userMobile));
        return user.getUserId();
    }

    public UserId create(UserMobile userMobile, UserPassword userPassword, UserId userId,
                         TransactionContext context) {
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        User user = User.newUser(userMobile, userPassword, userId);
        DomainRegistry.getUserRepository().add(user);
        context
            .append(new NewUserRegistered(user.getUserId(), userMobile));
        return user.getUserId();
    }

    public UserId create(UserEmail email, VerificationCode code, UserId userId,
                         TransactionContext context) {
        DomainRegistry.getTemporaryCodeService()
            .verifyCode(code.getValue(), VerificationCode.EXPIRE_AFTER_MILLI,
                VerificationCode.OPERATION_TYPE,
                email.getEmail());
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        User user = User.newUser(email, userId);
        DomainRegistry.getUserRepository().add(user);
        context
            .append(new NewUserRegistered(user.getUserId(), email));
        return user.getUserId();
    }

    public UserId create(UserEmail email, UserPassword userPassword, UserId userId,
                         TransactionContext context) {
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        User user = User.newUser(email, userPassword, userId);
        DomainRegistry.getUserRepository().add(user);
        context
            .append(new NewUserRegistered(user.getUserId(), email));
        return user.getUserId();
    }

    public UserId create(UserName username, UserPassword password, UserId userId,
                         TransactionContext context) {
        UserRelation.initNewUser(new RoleId(AppConstant.MT_AUTH_USER_ROLE_ID), userId,
            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
        User user = User.newUser(username, password, userId);
        DomainRegistry.getUserRepository().add(user);
        context
            .append(new NewUserRegistered(user.getUserId(), username));
        return user.getUserId();
    }
}
