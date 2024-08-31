package com.mt.integration;

import com.mt.integration.single.access.mgmt.MgmtClientTest;
import com.mt.integration.single.access.mgmt.MgmtEndpointTest;
import com.mt.integration.single.access.mgmt.MgmtProjectTest;
import com.mt.integration.single.access.mgmt.MgmtTest;
import com.mt.integration.single.access.mgmt.MgmtUserTest;
import com.mt.integration.single.access.mgmt.MgmtUtilityTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    MgmtClientTest.class,
    MgmtEndpointTest.class,
    MgmtProjectTest.class,
    MgmtTest.class,
    MgmtUserTest.class,
    MgmtUtilityTest.class,
})
public class MgmtTestSuite {
}
