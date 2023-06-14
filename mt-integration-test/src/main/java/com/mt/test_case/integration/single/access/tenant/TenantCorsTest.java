package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Cors;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.CorsUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.Utility;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantCorsTest extends TenantTest {
    @Test
    public void tenant_can_create_cors() {
        Cors randomCorsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, randomCorsObj);
        Assert.assertEquals(HttpStatus.OK, cors.getStatusCode());
        Assert.assertNotNull(UrlUtility.getId(cors));
    }

    @Test
    public void tenant_can_update_cors() {
        Cors corsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, corsObj);
        String corsId = UrlUtility.getId(cors);

        corsObj.setName(RandomUtility.randomStringWithNum());
        corsObj.setId(corsId);
        ResponseEntity<Void> cors2 = CorsUtility.updateTenantCors(tenantContext, corsObj);

        Assert.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        ResponseEntity<SumTotal<Cors>> read =
            CorsUtility.readTenantCors(tenantContext);
        List<Cors> collect = Objects.requireNonNull(read.getBody()).getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(corsId)).collect(
                Collectors.toList());
        Cors cors1 = collect.get(0);
        Assert.assertEquals(1, cors1.getVersion().intValue());
    }

    @Test
    public void tenant_can_view_cors_list() {
        ResponseEntity<SumTotal<Cors>> read =
            CorsUtility.readTenantCors(tenantContext);
        Assert.assertEquals(HttpStatus.OK, read.getStatusCode());
    }

    @Test
    public void tenant_can_delete_cors() {
        Cors cors1 = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, cors1);
        String corsId = UrlUtility.getId(cors);

        cors1.setId(corsId);
        ResponseEntity<Void> cors2 = CorsUtility.deleteTenantCors(tenantContext, cors1);
        Assert.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        ResponseEntity<SumTotal<Cors>> cors3 =
            CorsUtility.readTenantCors(tenantContext);
        Optional<Cors> first = cors3.getBody().getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(corsId)).findFirst();
        Assert.assertEquals(HttpStatus.OK, cors3.getStatusCode());
        Assert.assertTrue(first.isEmpty());
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
        Assert.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        Thread.sleep(10000);
        //read endpoint to verify cache id remove
        randomEndpointObj.setId(endpointId);
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assert.assertNull(endpointResponseEntity.getBody().getCorsProfileId());
    }

    @Test
    public void validation_create_valid(){
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response1 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }
    @Test
    public void validation_create_name() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setName(null);
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        cors.setName(" ");
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        cors.setName("");
        ResponseEntity<Void> response4 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //min length
        cors.setName("1");
        ResponseEntity<Void> response5 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.OK, response5.getStatusCode());
        //max length
        cors.setName("0123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response6 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid char
        cors.setName("<>");
        ResponseEntity<Void> response7 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_description() {
        Cors cors = CorsUtility.createValidCors();
        //blank
        cors.setDescription(" ");
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        cors.setDescription("");
        ResponseEntity<Void> response4 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        cors.setDescription(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response6 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid char
        cors.setDescription("<>");
        ResponseEntity<Void> response7 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_allow_credential() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setAllowCredentials(null);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_allowed_headers() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setAllowedHeaders(null);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //empty
        cors.setAllowedHeaders(Collections.emptySet());
        ResponseEntity<Void> response4 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //blank
        cors.setAllowedHeaders(Collections.singleton(" "));
        ResponseEntity<Void> response5 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid char
        cors.setAllowedHeaders(Collections.singleton("<>"));
        ResponseEntity<Void> response6 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid char
        cors.setAllowedHeaders(Collections.singleton("123"));
        ResponseEntity<Void> response8 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //max elements
        HashSet<String> strings = new HashSet<>();
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        cors.setAllowedHeaders(strings);
        ResponseEntity<Void> response7 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_allow_origin() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setAllowOrigin(null);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        cors.setAllowOrigin(Collections.singleton(""));
        ResponseEntity<Void> response4 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //blank
        cors.setAllowOrigin(Collections.singleton(" "));
        ResponseEntity<Void> response5 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format
        cors.setAllowOrigin(Collections.singleton(RandomUtility.randomStringWithNum()));
        ResponseEntity<Void> response6 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //max elements
        HashSet<String> strings = new HashSet<>();
        strings.add(RandomUtility.randomLocalHostUrl());
        strings.add(RandomUtility.randomLocalHostUrl());
        strings.add(RandomUtility.randomLocalHostUrl());
        strings.add(RandomUtility.randomLocalHostUrl());
        strings.add(RandomUtility.randomLocalHostUrl());
        strings.add(RandomUtility.randomLocalHostUrl());
        cors.setAllowOrigin(strings);
        ResponseEntity<Void> response7 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_exposed_headers() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setExposedHeaders(null);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //empty
        cors.setExposedHeaders(Collections.emptySet());
        ResponseEntity<Void> response4 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //blank
        cors.setExposedHeaders(Collections.singleton(" "));
        ResponseEntity<Void> response5 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid char
        cors.setExposedHeaders(Collections.singleton("<>"));
        ResponseEntity<Void> response6 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //max elements
        HashSet<String> strings = new HashSet<>();
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        strings.add(RandomUtility.randomStringWithNum());
        cors.setExposedHeaders(strings);
        ResponseEntity<Void> response7 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_max_age() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setMaxAge(null);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //min value
        cors.setMaxAge(1L);
        ResponseEntity<Void> response4 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max value
        cors.setMaxAge(Long.MAX_VALUE);
        ResponseEntity<Void> response5 = CorsUtility.createTenantCors(tenantContext, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        Cors cors = CorsUtility.createValidCors();
        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = CorsUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, cors);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = CorsUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.createResource(tenantContext.getCreator(), url3, cors);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = CorsUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.createResource(tenantContext.getCreator(), url4, cors);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = CorsUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.createResource(tenantContext.getCreator(), url2, cors);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_update_name() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_update_description() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_update_allow_credential() {
        //null
    }

    @Test
    public void validation_update_allowed_headers() {
        //null
        //empty
        //blank
        //invalid char
        //max elements
    }

    @Test
    public void validation_update_allow_origin() {
        //null
        //empty
        //blank
        //invalid format
        //max elements

    }

    @Test
    public void validation_update_exposed_headers() {
        //null
        //empty
        //blank
        //invalid char
        //max elements
    }

    @Test
    public void validation_update_max_age() {
        //null
        //min value
        //max value
    }

    @Test
    public void validation_patch_name() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_patch_description() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
