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
        Thread.sleep(10*1000);
        //read endpoint to verify cache id remove
        randomEndpointObj.setId(endpointId);
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assertions.assertNull(endpointResponseEntity.getBody().getCorsProfileId());
    }

    @Test
    public void validation_create_valid() {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response1 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_create_name(String name, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        cors.setName(name);
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_create_description(String description, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        cors.setDescription(description);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @Test
    public void validation_create_allow_credential() {
        Cors cors = CorsUtility.createValidCors();
        //null
        cors.setAllowCredentials(null);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsHeaderArgs.class)
    public void validation_create_allowed_headers(Set<String> ids, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        cors.setAllowedHeaders(ids);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsOriginArgs.class)
    public void validation_create_allow_origin(Set<String> origins, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        cors.setAllowOrigin(origins);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsHeaderArgs.class)
    public void validation_create_exposed_headers(Set<String> ids, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        cors.setExposedHeaders(ids);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsMaxAgeArgs.class)
    public void validation_create_max_age(Long maxAge, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        cors.setMaxAge(maxAge);
        ResponseEntity<Void> response3 = CorsUtility.createTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        //null
        Project project1 = new Project();
        project1.setId(projectId);
        String url = CorsUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, cors);
        Assertions.assertEquals(status, resource.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_update_name(String name, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        cors.setName(name);
        ResponseEntity<Void> response1 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        cors.setDescription(description);
        ResponseEntity<Void> response3 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @Test
    public void validation_update_allow_credential() {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        //null
        cors.setAllowCredentials(null);
        ResponseEntity<Void> response3 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsHeaderArgs.class)
    public void validation_update_allowed_headers(Set<String> ids, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        cors.setAllowedHeaders(ids);
        ResponseEntity<Void> response3 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsOriginArgs.class)
    public void validation_update_allow_origin(Set<String> origins, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        cors.setAllowOrigin(origins);
        ResponseEntity<Void> response3 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsHeaderArgs.class)
    public void validation_update_exposed_headers(Set<String> ids, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        cors.setExposedHeaders(ids);
        ResponseEntity<Void> response3 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsMaxAgeArgs.class)
    public void validation_update_max_age(Long maxAge, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        cors.setMaxAge(maxAge);
        ResponseEntity<Void> response3 = CorsUtility.updateTenantCors(tenantContext, cors);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_patch_name(String name, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        patchCommand.setValue(name);
        ResponseEntity<Void> response1 =
            CorsUtility.patchTenantCache(tenantContext, cors, patchCommand);
        Assertions.assertEquals(status, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_patch_description(String description, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        patchCommand.setValue(description);
        ResponseEntity<Void> response3 =
            CorsUtility.patchTenantCache(tenantContext, cors, patchCommand);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String projectId, HttpStatus status) {
        Cors cors = CorsUtility.createValidCors();
        ResponseEntity<Void> response2 = CorsUtility.createTenantCors(tenantContext, cors);
        cors.setId(UrlUtility.getId(response2));
        Project project1 = new Project();
        project1.setId(projectId);
        String url = CorsUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, cors, cors.getId());
        Assertions.assertEquals(status, resource.getStatusCode());
    }
}
