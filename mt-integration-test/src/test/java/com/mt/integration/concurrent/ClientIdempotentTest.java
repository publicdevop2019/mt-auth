package com.mt.integration.concurrent;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.ConcurrentUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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
public class ClientIdempotentTest {
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
    public void create_client_w_same_changeId_two_times() {
        String token = UserUtility.emailPwdLogin(tenantContext.getCreator());
        Client client = ClientUtility.getClientAsNonResource();
        String changeId = UUID.randomUUID().toString();
        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, token, client, changeId);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        ResponseEntity<Void> client2 =
            ClientUtility.createTenantClient(tenantContext, token, client, changeId);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
    }

    @Test
    public void create_client_then_update_w_same_changeId_two_times() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> createResp =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, createResp.getStatusCode());
        client.setId(HttpUtility.getId(createResp));
        client.setAccessTokenValiditySeconds(120);
        String changeId2 = UUID.randomUUID().toString();
        String token = UserUtility.emailPwdLogin(tenantContext.getCreator());
        client.setVersion(0);
        ResponseEntity<Void> client1 =
            ClientUtility.updateTenantClient(tenantContext, token, client, changeId2);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setVersion(1);
        ResponseEntity<Void> client2 =
            ClientUtility.updateTenantClient(tenantContext, token, client, changeId2);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
    }

    @Test
    public void create_client_then_delete_w_same_changeId_two_times() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> createResp =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, createResp.getStatusCode());
        client.setId(HttpUtility.getId(createResp));
        String changeId = UUID.randomUUID().toString();
        String token = UserUtility.emailPwdLogin(tenantContext.getCreator());
        ResponseEntity<Void> client1 =
            ClientUtility.deleteTenantClient(tenantContext, token, client, changeId);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, token, client, changeId);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
    }

    @Test
    public void create_client_w_same_changeId_two_times_concurrent() {
        Client client = ClientUtility.getClientAsNonResource();
        String changeId = UUID.randomUUID().toString();
        String token = UserUtility.emailPwdLogin(tenantContext.getCreator());
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<Void> client1 =
                ClientUtility.createTenantClient(tenantContext, token, client, changeId);
            if (client1.getStatusCode().is2xxSuccessful()) {
                success.set(success.get() + 1);
            }
            if (client1.getStatusCode().is4xxClientError()) {
                failed.set(failed.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertEquals(1, (int) success.get());
            Assertions.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_client_then_update_w_same_changeId_two_times_concurrent() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> createResp =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, createResp.getStatusCode());
        client.setId(HttpUtility.getId(createResp));
        client.setAccessTokenValiditySeconds(120);
        client.setVersion(0);
        String changeId = UUID.randomUUID().toString();
        String token = UserUtility.emailPwdLogin(tenantContext.getCreator());
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<Void> exchange =
                ClientUtility.updateTenantClient(tenantContext, token, client, changeId);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                success.set(success.get() + 1);
            }
            if (exchange.getStatusCode().is4xxClientError()) {
                failed.set(failed.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertEquals(1, (int) success.get());
            Assertions.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_client_then_delete_w_same_changeId_two_times_concurrent() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> createResp =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, createResp.getStatusCode());
        client.setId(HttpUtility.getId(createResp));
        String changeId = UUID.randomUUID().toString();
        String token = UserUtility.emailPwdLogin(tenantContext.getCreator());
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<Void> exchange =
                ClientUtility.deleteTenantClient(tenantContext, token, client, changeId);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                success.set(success.get() + 1);
            }
            if (exchange.getStatusCode().is4xxClientError()) {
                failed.set(failed.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertEquals(1, (int) success.get());
            Assertions.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
