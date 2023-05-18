package com.mt.test_case.helper;

import com.mt.test_case.helper.utility.TenantUtility;
import com.mt.test_case.helper.utility.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
@Slf4j
public class TenantTest extends CommonTest {
    protected static TenantContext tenantContext;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        log.info("init tenant complete");
    }
}