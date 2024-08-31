package com.mt.integration;

import com.mt.integration.single.access.user.UserLoginTest;
import com.mt.integration.single.access.user.UserProfileTest;
import com.mt.integration.single.access.user.validation.UserValidationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    UserLoginTest.class,
    UserProfileTest.class,
    UserValidationTest.class,
})
public class UserTestSuite {
}
