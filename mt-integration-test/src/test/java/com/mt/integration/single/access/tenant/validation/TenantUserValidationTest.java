package com.mt.integration.single.access.tenant.validation;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.UserRoleArgs;
import com.mt.helper.pojo.AssignRoleReq;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.UserUtility;
import java.util.HashSet;
import java.util.List;
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
public class TenantUserValidationTest {
    private static TenantContext tenantContext;

    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @ParameterizedTest
    @ArgumentsSource(UserRoleArgs.class)
    public void validation_assign_user_role_ids(List<String> roles, HttpStatus httpStatus) {
        User user = tenantContext.getUsers().get(0);
        AssignRoleReq assignRoleReq = new AssignRoleReq();
        if (roles == null) {
            assignRoleReq.setRoleIds(null);
        } else {
            assignRoleReq.setRoleIds(new HashSet<>(roles));
        }
        ResponseEntity<Void> response =
            UserUtility.assignTenantUserRole(tenantContext, user, assignRoleReq);
        Assertions.assertEquals(httpStatus, response.getStatusCode());
    }
}
