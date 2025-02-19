package com.mt.integration.single.access.tenant;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.AssignRoleReq;
import com.mt.helper.pojo.Cache;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Cors;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.Role;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.UpdateType;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.CacheUtility;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.CorsUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.UserUtility;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

/**
 * when update resource with same payload multiple times, version should not change
 */
@Slf4j
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
public class VersionTest {
    private static TenantContext tenantContext;
    private static Client client;
    private static Role rootRole;
    private static Endpoint sharedEndpointObj;

    @BeforeAll
    public static void initTenant() {
        TestHelper.beforeAll(log);
        log.info("init tenant in progress");
        tenantContext = TenantUtility.initTenant();
        //create root node
        rootRole = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole = RoleUtility.createTenantRole(tenantContext, rootRole);
        rootRole.setId(HttpUtility.getId(tenantRole));
        log.info("init tenant complete");

        //create client for endpoint
        client = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        String clientId = HttpUtility.getId(tenantClient);
        client.setId(clientId);
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createValidSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(HttpUtility.getId(tenantEndpoint2));
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void client_version_will_not_increase() {
        Client client = ClientUtility.createValidBackendClient();
        client.setClientSecret(client.getClientSecret());
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(HttpUtility.getId(tenantClient));
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Void> client3 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client3.getStatusCode());
        ResponseEntity<Client> clientResponseEntity =
            ClientUtility.readTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, clientResponseEntity.getStatusCode());
        Assertions.assertEquals(0, (int) clientResponseEntity.getBody().getVersion());
    }

    @Test
    public void cache_version_will_not_increase() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = HttpUtility.getId(cache);
        cacheObj.setId(cacheId);
        CacheUtility.updateTenantCache(tenantContext, cacheObj);
        CacheUtility.updateTenantCache(tenantContext, cacheObj);
        ResponseEntity<SumTotal<Cache>> read =
            CacheUtility.readTenantCache(tenantContext);
        List<Cache> collect = Objects.requireNonNull(read.getBody()).getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(cacheId)).collect(
                Collectors.toList());
        Cache cache1 = collect.get(0);
        Assertions.assertEquals(0, cache1.getVersion().intValue());
    }

    @Test
    public void cors_version_will_not_increase() {
        Cors corsObj = CorsUtility.createValidCors();
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, corsObj);
        String corsId = HttpUtility.getId(cors);

        corsObj.setId(corsId);
        ResponseEntity<Void> cors2 = CorsUtility.updateTenantCors(tenantContext, corsObj);

        Assertions.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        ResponseEntity<SumTotal<Cors>> read =
            CorsUtility.readTenantCors(tenantContext);
        List<Cors> collect = Objects.requireNonNull(read.getBody()).getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(corsId)).collect(
                Collectors.toList());
        Cors cors1 = collect.get(0);
        Assertions.assertEquals(0, cors1.getVersion().intValue());
    }

    //allow headers and exposed headers are empty
    @Test
    public void cors_version_will_not_increase_empty_headers() {
        Cors corsObj = CorsUtility.createValidCors();
        corsObj.setAllowedHeaders(null);
        corsObj.setExposedHeaders(null);
        ResponseEntity<Void> cors = CorsUtility.createTenantCors(tenantContext, corsObj);
        String corsId = HttpUtility.getId(cors);

        corsObj.setId(corsId);
        ResponseEntity<Void> cors2 = CorsUtility.updateTenantCors(tenantContext, corsObj);

        Assertions.assertEquals(HttpStatus.OK, cors2.getStatusCode());
        ResponseEntity<SumTotal<Cors>> read =
            CorsUtility.readTenantCors(tenantContext);
        List<Cors> collect = Objects.requireNonNull(read.getBody()).getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(corsId)).collect(
                Collectors.toList());
        Cors cors1 = collect.get(0);
        Assertions.assertEquals(0, cors1.getVersion().intValue());
    }

    @Test
    public void endpoint_version_will_not_increase() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        endpoint.setName(RandomUtility.randomStringWithNum());
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //update endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint
            );
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        //update endpoint
        ResponseEntity<Void> voidResponseEntity2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint
            );
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(1, endpointResponseEntity.getBody().getVersion().intValue());
    }

    @Test
    public void role_version_will_not_increase() {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(tenantRole));
        //update basic info
        role.setName(RandomUtility.randomStringWithNum());
        role.setType(UpdateType.BASIC.name());
        ResponseEntity<Void> response6 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response6.getStatusCode());
        ResponseEntity<Void> response7 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response7.getStatusCode());
        //read again
        ResponseEntity<Role> response8 =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response8.getStatusCode());
        Assertions.assertEquals(1, response8.getBody().getVersion().intValue());
    }


    @Test
    public void tenant_user_version_will_not_increase() {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(tenantRole));
        //read user
        User user = tenantContext.getUsers().get(0);
        AssignRoleReq assignRoleReq = new AssignRoleReq();
        ResponseEntity<User> userResponseEntity = UserUtility.readTenantUser(tenantContext, user);
        User currentUser = userResponseEntity.getBody();
        assignRoleReq.setRoleIds(
            currentUser.getRoleDetails().stream().map(User.RoleDetail::getId).sorted().collect(
                Collectors.toCollection(LinkedHashSet::new)));
        //assign role
        ResponseEntity<Void> voidResponseEntity =
            UserUtility.assignTenantUserRole(tenantContext, user, assignRoleReq);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        //do same update
        ResponseEntity<Void> voidResponseEntity2 =
            UserUtility.assignTenantUserRole(tenantContext, user, assignRoleReq);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
        //read user, version should be 0
        ResponseEntity<User> response = UserUtility.readTenantUser(tenantContext, user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(0, response.getBody().getVersion().intValue());
    }
}
