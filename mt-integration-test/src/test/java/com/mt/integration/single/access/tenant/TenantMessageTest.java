package com.mt.integration.single.access.tenant;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.Notification;
import com.mt.helper.pojo.SubscriptionReq;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.MarketUtility;
import com.mt.helper.utility.MessageUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.HttpUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantMessageTest {
    protected static TenantContext tenantContextA;
    protected static TenantContext tenantContextB;
    protected static Client clientA;

    @BeforeAll
    public static void initTenant() {
        TestHelper.beforeAll(log);
        log.info("init tenant in progress");
        tenantContextA = TenantUtility.initTenant();
        tenantContextB = TenantUtility.initTenant();

        clientA = ClientUtility.createValidBackendClient();
        clientA.setResourceIndicator(true);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContextA, clientA);
        clientA.setId(HttpUtility.getId(tenantClient));

        log.info("init tenant complete");
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }
    @Test
    public void tenant_can_get_endpoint_expire_msg() throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = HttpUtility.getId(voidResponseEntity);
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);
        //wait for cache to expire
        Thread.sleep(5 * 1000);
        EndpointUtility.expireTenantEndpoint(tenantContextA, endpoint);
        Thread.sleep(5 * 1000);
        ResponseEntity<SumTotal<Notification>> sumTotalResponseEntity =
            MessageUtility.readMessages(tenantContextB.getCreator());
        Assertions.assertEquals(1, sumTotalResponseEntity.getBody().getData().size());
    }
}
