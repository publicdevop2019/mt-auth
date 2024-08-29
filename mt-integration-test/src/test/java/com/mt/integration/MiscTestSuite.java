package com.mt.integration;

import com.mt.integration.concurrent.ClientIdempotentTest;
import com.mt.integration.concurrent.GatewayFilterConcurrentTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({

    ClientIdempotentTest.class,
    GatewayFilterConcurrentTest.class
})
public class MiscTestSuite {
}
