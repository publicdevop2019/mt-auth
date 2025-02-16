package com.mt.integration.single.access.tenant.validation;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.ProjectNameArgs;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ProjectUtility;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("validation")

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantProjectValidationTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectNameArgs.class)
    public void validation_create_name(String name, HttpStatus httpStatus) {
        User user = UserUtility.createEmailPwdUser();
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        randomProjectObj.setName(name);
        ResponseEntity<Void> response =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assertions.assertEquals(httpStatus, response.getStatusCode());
    }
}
