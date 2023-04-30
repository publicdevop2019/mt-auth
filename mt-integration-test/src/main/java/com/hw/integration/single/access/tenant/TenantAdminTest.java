package com.hw.integration.single.access.tenant;

import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantAdminTest extends CommonTest {
    @Test
    public void tenant_can_add_admin() {
    }

    @Test
    public void tenant_can_remove_admin() {
    }

    @Test
    public void tenant_cannot_add_user_not_using_project_as_admin() {
    }
}
