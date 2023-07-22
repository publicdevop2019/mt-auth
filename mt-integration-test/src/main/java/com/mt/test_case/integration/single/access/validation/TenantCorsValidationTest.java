package com.mt.test_case.integration.single.access.validation;

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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
public class TenantCorsValidationTest extends TenantTest {

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
