package com.mt.helper;

import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.TestContext;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class TestHelper {

    public static final String TEST_ID = "TEST_ID";
    public static final String RUN_ID = "RUN_ID";

    public static void beforeEach(Logger log) {
        MDC.put(TEST_ID, UUID.randomUUID().toString());
        log.info("test id {}", MDC.get(TEST_ID));
    }

    public static void beforeAll(Logger log) {
        TestContext.init();
        String s = UUID.randomUUID().toString();
        MDC.clear();
        MDC.put(RUN_ID, s);
        log.info("run id {}", s);
    }

    public static TenantContext beforeAllTenant(Logger log) {
        beforeAll(log);
        log.info("init tenant started");
        TenantContext tenantContext = TenantUtility.initTenant();
        log.info("init tenant complete");
        return tenantContext;
    }
}
