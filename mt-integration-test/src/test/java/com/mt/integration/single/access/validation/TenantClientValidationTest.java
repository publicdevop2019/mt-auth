package com.mt.integration.single.access.validation;

import com.mt.helper.TenantTest;
import com.mt.helper.args.ClientAutoApproveArgs;
import com.mt.helper.args.ClientExternalUrlArgs;
import com.mt.helper.args.ClientGrantTypeArgs;
import com.mt.helper.args.ClientNameArgs;
import com.mt.helper.args.ClientPathArgs;
import com.mt.helper.args.ClientRedirectUriArgs;
import com.mt.helper.args.ClientRedirectUriExtArgs;
import com.mt.helper.args.ClientRefreshGrantArgs;
import com.mt.helper.args.ClientRefreshTokenSecondArgs;
import com.mt.helper.args.ClientResourceIdsArgs;
import com.mt.helper.args.ClientResourceIndicatorArgs;
import com.mt.helper.args.ClientSecretArgs;
import com.mt.helper.args.ClientTokenSecondArgs;
import com.mt.helper.args.ClientTypeArgs;
import com.mt.helper.args.DescriptionArgs;
import com.mt.helper.args.ProjectIdArgs;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.GrantType;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.Utility;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@Tag("validation")

@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantClientValidationTest extends TenantTest {
    @Test
    public void validation_create_valid_backend() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    public void validation_create_valid_frontend() {
        Client client = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientNameArgs.class)
    public void validation_create_name(String name, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setName(name);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_create_description(String description, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setDescription(description);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientSecretArgs.class)
    public void validation_create_secret(Client client, String secret, HttpStatus status) {
        client.setHasSecret(true);
        client.setClientSecret(secret);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response4.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        project.setId(projectId);
        String url = ClientUtility.getUrl(project);
        ResponseEntity<Void> response2 =
            Utility.createResource(tenantContext.getCreator(), url, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientPathArgs.class)
    public void validation_create_path(Client client, String path, HttpStatus status) {
        client.setPath(path);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @Test
    public void validation_create_path_unique_across_application() {
        Client client = ClientUtility.createValidBackendClient();
        String repeatedPath = client.getPath();
        //unique across application
        client.setPath(repeatedPath);
        ResponseEntity<Void> response8 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response8.getStatusCode());
        ResponseEntity<Void> response11 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response11.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientExternalUrlArgs.class)
    public void validation_create_external_url(Client client, String externalUrl,
                                               HttpStatus status) {
        client.setExternalUrl(externalUrl);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientGrantTypeArgs.class)
    public void validation_create_grant_type(Set<String> grantTypes, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setGrantTypeEnums(grantTypes);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRedirectUriArgs.class)
    public void validation_create_grant_type_authorization_grant(
        Set<String> ids, HttpStatus status) {
        Client client1 = ClientUtility.createAuthorizationClientObj();
        client1.setRegisteredRedirectUri(ids);
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(status, response7.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRefreshGrantArgs.class)
    public void validation_create_grant_type_refresh_grant(Integer sec,
                                                           HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        //refresh grant but refresh token is 0
        HashSet<String> strings = new HashSet<>();
        strings.add(GrantType.PASSWORD.name());
        strings.add(GrantType.REFRESH_TOKEN.name());
        client.setGrantTypeEnums(strings);
        client.setRefreshTokenValiditySeconds(sec);
        ResponseEntity<Void> response9 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response9.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientTypeArgs.class)
    public void validation_create_type(Set<String> types, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setTypes(types);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientTokenSecondArgs.class)
    public void validation_create_access_token_validity_second(Integer integer, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setAccessTokenValiditySeconds(integer);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRedirectUriExtArgs.class)
    public void validation_create_registered_redirect_url(
        Set<String> grantTypes, Set<String> urls, HttpStatus status
    ) {

        Client client = ClientUtility.createAuthorizationClientObj();
        client.setGrantTypeEnums(grantTypes);
        client.setRegisteredRedirectUri(urls);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRefreshTokenSecondArgs.class)
    public void validation_create_refresh_token_validity_second(Set<String> grantType,
                                                                Integer integer,
                                                                HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setGrantTypeEnums(grantType);
        client.setRefreshTokenValiditySeconds(integer);
        ResponseEntity<Void> response =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientResourceIdsArgs.class)
    public void validation_create_resource_ids(Set<String> ids,
                                               HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        client.setResourceIds(ids);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());

    }

    @ParameterizedTest
    @ArgumentsSource(ClientResourceIndicatorArgs.class)
    public void validation_create_resource_indicator_null(Client client, Boolean indicator,
                                                          HttpStatus status) {
        client.setResourceIndicator(indicator);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(ClientAutoApproveArgs.class)
    public void validation_create_auto_approve(Boolean autoApprove,
                                               Set<String> grantTypes,
                                               Set<String> redirectUrls,
                                               HttpStatus status
    ) {
        Client client = ClientUtility.createAuthorizationClientObj();
        client.setAutoApprove(autoApprove);
        client.setGrantTypeEnums(grantTypes);
        client.setRegisteredRedirectUri(redirectUrls);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response4.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String projectId, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Project project = new Project();
        project.setId(projectId);
        String url = ClientUtility.getUrl(project);
        ResponseEntity<Void> response2 =
            Utility.updateResource(tenantContext.getCreator(), url, client, client.getId());
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @Test
    public void validation_update_secret_type_is_backend_and_secret_is_missing_then_secret_will_not_change() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));

        //type is backend and secret is missing, then secret will not change
        client.setHasSecret(true);
        client.setClientSecret(null);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
    }

    @Test
    public void validation_update_secret_type_is_frontend_but_secret_is_present() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //type is frontend but secret is present
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        client1.setId(UrlUtility.getId(response2));
        client1.setClientSecret("test");
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    public void validation_update_secret_format() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //secret format
        client.setHasSecret(true);
        client.setClientSecret("0123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response5.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setDescription(description);
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientPathArgs.class)
    public void validation_update_path(Client client, String path, HttpStatus status) {
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setPath(path);
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(ClientExternalUrlArgs.class)
    public void validation_update_external_url(Client client, String externalUrl,
                                               HttpStatus status) {
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setExternalUrl(externalUrl);
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientGrantTypeArgs.class)
    public void validation_update_grant_type(Set<String> grantTypes, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setGrantTypeEnums(grantTypes);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response4.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRedirectUriArgs.class)
    public void validation_update_grant_type_authorization_grant(
        Set<String> urls, HttpStatus status) {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response2));
        client.setRegisteredRedirectUri(urls);
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response7.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRefreshGrantArgs.class)
    public void validation_update_grant_type_refresh_grant(Integer sec,
                                                           HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        HashSet<String> strings = new HashSet<>();
        strings.add(GrantType.PASSWORD.name());
        strings.add(GrantType.REFRESH_TOKEN.name());
        client.setGrantTypeEnums(strings);
        client.setRefreshTokenValiditySeconds(sec);
        ResponseEntity<Void> response9 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response9.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientTokenSecondArgs.class)
    public void validation_update_access_token_validity_second(Integer integer, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setAccessTokenValiditySeconds(integer);
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRedirectUriExtArgs.class)
    public void validation_update_registered_redirect_url(
        Set<String> grantTypes,
        Set<String> urls,
        HttpStatus status
    ) {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setGrantTypeEnums(grantTypes);
        client.setRegisteredRedirectUri(urls);
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientRefreshTokenSecondArgs.class)
    public void validation_update_refresh_token_validity_second(Set<String> grantType,
                                                                Integer integer,
                                                                HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response0));

        client.setGrantTypeEnums(grantType);
        client.setRefreshTokenValiditySeconds(integer);
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientResourceIdsArgs.class)
    public void validation_update_resource_ids(Set<String> ids,
                                               HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setResourceIds(ids);
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientResourceIndicatorArgs.class)
    public void validation_update_resource_indicator(Client client, Boolean indicator,
                                                     HttpStatus status) {
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setResourceIndicator(indicator);
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response5.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientAutoApproveArgs.class)
    public void validation_update_auto_approve(Boolean autoApprove,
                                               Set<String> grantTypes,
                                               Set<String> redirectUrls,
                                               HttpStatus status
    ) {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        client.setAutoApprove(autoApprove);
        client.setGrantTypeEnums(grantTypes);
        client.setRegisteredRedirectUri(redirectUrls);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(status, response4.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_patch_description(String description, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        patchCommand.setValue(description);
        ResponseEntity<Void> response2 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientPathArgs.class)
    public void validation_patch_path(Client client, String path, HttpStatus status) {
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/path");
        patchCommand.setValue(path);
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientGrantTypeArgs.class)
    public void validation_patch_grant_type(Set<String> grantTypes, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/grantTypeEnums");
        patchCommand.setValue(grantTypes);
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(status, response3.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientTokenSecondArgs.class)
    public void validation_patch_access_token_validity_second(Integer integer, HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/accessTokenValiditySeconds");
        patchCommand.setValue(integer);
        ResponseEntity<Void> response2 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientResourceIdsArgs.class)
    public void validation_patch_resource_ids(Set<String> ids,
                                              HttpStatus status) {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/resourceIds");
        patchCommand.setValue(ids);
        ResponseEntity<Void> response2 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientResourceIndicatorArgs.class)
    public void validation_patch_resource_indicator(Client client, Boolean indicator,
                                                    HttpStatus status) {
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/resourceIndicator");
        patchCommand.setValue(indicator);
        ResponseEntity<Void> response5 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(status, response5.getStatusCode());
    }
}