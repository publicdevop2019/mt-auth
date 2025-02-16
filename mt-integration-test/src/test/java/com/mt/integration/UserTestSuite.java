package com.mt.integration;

import com.mt.integration.single.access.user.UserLoginTest;
import com.mt.integration.single.access.user.UserProfileTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    UserLoginTest.class,
    UserProfileTest.class,
})
public class UserTestSuite {
}
