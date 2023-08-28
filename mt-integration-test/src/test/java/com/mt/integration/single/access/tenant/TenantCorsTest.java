package com.mt.integration.single.access.tenant;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Cors;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.CorsUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.HttpUtility;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantCorsTest{
    private static TenantContext tenantContext;
    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }
    @Test
    public void tenant_can_create_cors() {
        Cors randomCorsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, randomCorsObj);
        Assertions.assertEquals(HttpStatus.OK, cors.getStatusCode());
        Assertions.assertNotNull(HttpUtility.getId(cors));
    }

    @Test
    public void tenant_can_update_cors() {
        Cors corsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, corsObj);
        String corsId = HttpUtility.getId(cors);

        corsObj.setName(RandomUtility.randomStringWithNum());
        corsObj.setId(corsId);
        ResponseEntity<Void> cors2 = CorsUtility.updateTenantCors(tenantContext, corsObj);

        Assertions.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        ResponseEntity<SumTotal<Cors>> read =
            CorsUtility.readTenantCorsById(tenantContext,corsId);
        Cors cors1 = Objects.requireNonNull(read.getBody()).getData().get(0);
        Assertions.assertEquals(1, cors1.getVersion().intValue());
    }

    @Test
    public void tenant_can_view_cors_list() {
        ResponseEntity<SumTotal<Cors>> read =
            CorsUtility.readTenantCors(tenantContext);
        Assertions.assertEquals(HttpStatus.OK, read.getStatusCode());
    }

    @Test
    public void tenant_can_delete_cors() {
        Cors cors1 = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, cors1);
        String corsId = HttpUtility.getId(cors);

        cors1.setId(corsId);
        ResponseEntity<Void> cors2 = CorsUtility.deleteTenantCors(tenantContext, cors1);
        Assertions.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        ResponseEntity<SumTotal<Cors>> cors3 =
            CorsUtility.readTenantCors(tenantContext);
        Optional<Cors> first = cors3.getBody().getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(corsId)).findFirst();
        Assertions.assertEquals(HttpStatus.OK, cors3.getStatusCode());
        Assertions.assertTrue(first.isEmpty());
    }


    @Test
    public void tenant_can_delete_assigned_cors() throws InterruptedException {
        //create cors
        Cors corsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, corsObj);
        String corsId = HttpUtility.getId(cors);
        corsObj.setId(corsId);
        //create backend client
        Client randomClient = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = HttpUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createValidGetEndpoint(clientId);
        randomEndpointObj.setCorsProfileId(corsId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        String endpointId = HttpUtility.getId(tenantEndpoint);
        //delete cors
        ResponseEntity<Void> cors2 = CorsUtility.deleteTenantCors(tenantContext, corsObj);
        Assertions.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        Thread.sleep(5*1000);
        //read endpoint to verify cache id remove
        randomEndpointObj.setId(endpointId);
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assertions.assertNull(endpointResponseEntity.getBody().getCorsProfileId());
    }


}
