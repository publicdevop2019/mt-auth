package com.hw.integration.single.access.tenant;

import com.hw.helper.Client;
import com.hw.helper.Endpoint;
import com.hw.helper.Notification;
import com.hw.helper.SubscriptionReq;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.MarketUtility;
import com.hw.helper.utility.MessageUtility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantMessageTest {
    protected static TenantUtility.TenantContext tenantContextA;
    protected static TenantUtility.TenantContext tenantContextB;
    protected static Client clientA;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContextA = TenantUtility.initTenant();
        tenantContextB = TenantUtility.initTenant();

        clientA = ClientUtility.createRandomBackendClientObj();
        clientA.setResourceIndicator(true);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContextA, clientA);
        clientA.setId(tenantClient.getHeaders().getLocation().toString());

        log.info("init tenant complete");
    }

    @Test
    public void tenant_can_get_endpoint_expire_msg() throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createRandomSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(tenantEndpoint.getHeaders().getLocation().toString());
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createRandomTenantSubReqObj(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = voidResponseEntity.getHeaders().getLocation().toString();
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);
        //wait for cache to expire
        Thread.sleep(20*1000);
        EndpointUtility.expireTenantEndpoint(tenantContextA, endpoint);
        Thread.sleep(10*1000);
        ResponseEntity<SumTotal<Notification>> sumTotalResponseEntity =
            MessageUtility.readMessages(tenantContextB.getCreator());
        Assert.assertEquals(1, sumTotalResponseEntity.getBody().getData().size());
    }
}
