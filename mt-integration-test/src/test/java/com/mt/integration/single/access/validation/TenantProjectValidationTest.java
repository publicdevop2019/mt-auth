package com.mt.integration.single.access.validation;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.ProjectNameArgs;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ProjectUtility;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@Tag("validation")

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantProjectValidationTest{
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }
    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }
    @ParameterizedTest
    @ArgumentsSource(ProjectNameArgs.class)
    public void validation_create_name(String name, HttpStatus httpStatus) {
        User user = UserUtility.createUser();
        Project randomProjectObj = ProjectUtility.createRandomProjectObj();
        randomProjectObj.setName(name);
        ResponseEntity<Void> response =
            ProjectUtility.createTenantProject(randomProjectObj, user);
        Assertions.assertEquals(httpStatus, response.getStatusCode());
    }

    //endpoint note added
    @Disabled
    @ParameterizedTest
    @ArgumentsSource(ProjectNameArgs.class)
    public void validation_update_name(String name, HttpStatus httpStatus) {
        User user = UserUtility.createUser();
        Project project = ProjectUtility.createRandomProjectObj();
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(project, user);
        project.setId(UrlUtility.getId(tenantProject));
        project.setName(name);
        ResponseEntity<Void> response =
            ProjectUtility.updateTenantProject(project, user);
        Assertions.assertEquals(httpStatus, response.getStatusCode());
    }

    //endpoint note added
    @Disabled
    @ParameterizedTest
    @ArgumentsSource(ProjectNameArgs.class)
    public void validation_patch_name(String name, HttpStatus httpStatus) {
        User user = UserUtility.createUser();
        Project project = ProjectUtility.createRandomProjectObj();
        ResponseEntity<Void> tenantProject =
            ProjectUtility.createTenantProject(project, user);
        project.setId(UrlUtility.getId(tenantProject));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        patchCommand.setValue(name);
        ResponseEntity<Void> response =
            ProjectUtility.patchTenantProject(project, user, patchCommand);
        Assertions.assertEquals(httpStatus, response.getStatusCode());
    }
}
