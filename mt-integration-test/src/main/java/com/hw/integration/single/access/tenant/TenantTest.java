package com.hw.integration.single.access.tenant;

import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
@Slf4j
public class TenantTest extends CommonTest {
    protected static TenantUtility.TenantContext tenantContext;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        log.info("init tenant complete");
    }
}
