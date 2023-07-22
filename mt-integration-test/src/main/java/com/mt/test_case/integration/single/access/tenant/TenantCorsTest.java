package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.args.CorsHeaderArgs;
import com.mt.test_case.helper.args.CorsMaxAgeArgs;
import com.mt.test_case.helper.args.CorsOriginArgs;
import com.mt.test_case.helper.args.DescriptionArgs;
import com.mt.test_case.helper.args.NameArgs;
import com.mt.test_case.helper.args.ProjectIdArgs;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Cors;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.CorsUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.Utility;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantCorsTest extends TenantTest {
    @Test
    public void tenant_can_create_cors() {
        Cors randomCorsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, randomCorsObj);
        Assertions.assertEquals(HttpStatus.OK, cors.getStatusCode());
        Assertions.assertNotNull(UrlUtility.getId(cors));
    }

    @Test
    public void tenant_can_update_cors() {
        Cors corsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, corsObj);
        String corsId = UrlUtility.getId(cors);

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
        String corsId = UrlUtility.getId(cors);

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
        String corsId = UrlUtility.getId(cors);
        corsObj.setId(corsId);
        //create backend client
        Client randomClient = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = UrlUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createValidGetEndpoint(clientId);
        randomEndpointObj.setCorsProfileId(corsId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        String endpointId = UrlUtility.getId(tenantEndpoint);
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
