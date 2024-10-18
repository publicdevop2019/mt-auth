package com.mt.integration;

import com.mt.integration.single.access.user.UserLoginTest;
import com.mt.integration.single.access.user.UserProfileTest;
import com.mt.integration.single.access.user.validation.UserForgetPwdValidationTest;
import com.mt.integration.single.access.user.validation.UserLoginValidationTest;
import com.mt.integration.single.access.user.validation.UserProfileValidationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    UserProfileValidationTest.class,
    UserForgetPwdValidationTest.class,
    UserLoginValidationTest.class
})
public class UserValidationTestSuite {
}
