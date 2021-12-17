package com.hw.integration.mall;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.hw.helper.UserAction.*;
import static com.hw.integration.mall.ProductTest.PRODUCTS_ADMIN;
import static com.hw.integration.mall.ProductTest.PRODUCTS_PUBLIC;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class ProductConcurrentTest {
    public static final String URL_2 = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/products/app";
    @Autowired
    UserAction action;
    ObjectMapper mapper = new ObjectMapper();
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void create_product_then_concurrent_decrease_order_storage() {
        AtomicInteger iniOrderStorage = new AtomicInteger(1000);
        ResponseEntity<String> exchange = action.createRandomProductDetail(null, iniOrderStorage.get());
        String productId = exchange.getHeaders().getLocation().toString();
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("diff");
        patchCommand.setExpect(1);
        patchCommand.setValue(1);
//        /837195323695104/skus?query=attributesSales:835604723556352-淡粉色/storageActual
        patchCommand.setPath("/" + productId + "/skus?query=" + "attributesSales:" + TEST_TEST_VALUE.replace(":", "-") + "/storageOrder");
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        patchCommands.add(patchCommand);
        Integer threadCount = 50;
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(action.getJwtClientCredential(CLIENT_ID_SAGA_ID, COMMON_CLIENT_SECRET).getBody().getValue());
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(200);
        integers.add(400);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpEntity<ArrayList<PatchCommand>> listHttpEntity = new HttpEntity<>(patchCommands, headers2);
                ResponseEntity<Object> exchange2 = action.restTemplate.exchange(URL_2, HttpMethod.PATCH, listHttpEntity, Object.class);
                if (exchange.getStatusCodeValue() == 200) {
                    iniOrderStorage.decrementAndGet();
                }
                Assert.assertTrue("expected status code but is " + exchange2.getStatusCodeValue(), integers.contains(exchange2.getStatusCodeValue()));
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, threadCount).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
            // get product order count
            Thread.sleep(60*1000);
            ResponseEntity<ProductDetailAdminRepresentation> productDetailAdminRepresentationResponseEntity = action.readProductDetailByIdAdmin(productId);
            assertTrue("remain storage should be " + iniOrderStorage.get() + " but is " + productDetailAdminRepresentationResponseEntity.getBody().getSkus().get(0).getStorageOrder(), productDetailAdminRepresentationResponseEntity.getBody().getSkus().get(0).getStorageOrder().equals(iniOrderStorage.get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * if lock is pessimistic then deadlock exception will detected
     */
    public void create_three_product_then_concurrent_decrease_diff_product_concurrent(Integer initialStorage, Integer threads) throws InterruptedException {
        AtomicInteger iniOrderStorage = new AtomicInteger(initialStorage);
        AtomicInteger iniOrderStorage2 = new AtomicInteger(initialStorage);
        AtomicInteger iniOrderStorage3 = new AtomicInteger(initialStorage);
        ResponseEntity<String> exchange = action.createRandomProductDetail(null, initialStorage);
        ResponseEntity<String> exchange2 = action.createRandomProductDetail(null, initialStorage);
        ResponseEntity<String> exchange3 = action.createRandomProductDetail(null, initialStorage);
        Thread.sleep(2000);
        String productId = exchange.getHeaders().getLocation().toString();
        String productId2 = exchange2.getHeaders().getLocation().toString();
        String productId3 = exchange3.getHeaders().getLocation().toString();

        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        PatchCommand patchCommand = new PatchCommand();
        patchCommands.add(patchCommand);
        patchCommand.setOp("diff");
        patchCommand.setExpect(1);
        patchCommand.setValue(1);
        patchCommand.setPath("/" + productId + "/skus?query=" + "attributesSales:" + TEST_TEST_VALUE.replace(":", "-") + "/storageOrder");
        PatchCommand patchCommand2 = new PatchCommand();
        patchCommands.add(patchCommand2);
        patchCommand2.setOp("diff");
        patchCommand2.setExpect(1);
        patchCommand2.setValue(1);
        patchCommand2.setPath("/" + productId2 + "/skus?query=" + "attributesSales:" + TEST_TEST_VALUE.replace(":", "-") + "/storageOrder");
        PatchCommand patchCommand3 = new PatchCommand();
        patchCommands.add(patchCommand3);
        patchCommand3.setOp("diff");
        patchCommand3.setExpect(1);
        patchCommand3.setValue(1);
        patchCommand3.setPath("/" + productId3 + "/skus?query=" + "attributesSales:" + TEST_TEST_VALUE.replace(":", "-") + "/storageOrder");

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(action.getJwtClientCredential(CLIENT_ID_USER_PROFILE_ID, COMMON_CLIENT_SECRET).getBody().getValue());
        headers2.setContentType(MediaType.APPLICATION_JSON);
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(200);
        integers.add(400);
        integers.add(500);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpEntity<List<PatchCommand>> listHttpEntity = new HttpEntity<>(patchCommands, headers2);
                ResponseEntity<Object> exchange = action.restTemplate.exchange(URL_2, HttpMethod.PATCH, listHttpEntity, Object.class);
                if (exchange.getStatusCodeValue() == 200) {
                    iniOrderStorage.decrementAndGet();
                    iniOrderStorage2.decrementAndGet();
                    iniOrderStorage3.decrementAndGet();
                }
                Assert.assertTrue("expected status code but is " + exchange.getStatusCodeValue(), integers.contains(exchange.getStatusCodeValue()));
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                HttpEntity<List<PatchCommand>> listHttpEntity2 = new HttpEntity<>(patchCommands, headers2);
                ResponseEntity<Object> exchange = action.restTemplate.exchange(URL_2, HttpMethod.PATCH, listHttpEntity2, Object.class);
                if (exchange.getStatusCodeValue() == 200) {
                    iniOrderStorage.decrementAndGet();
                    iniOrderStorage2.decrementAndGet();
                    iniOrderStorage3.decrementAndGet();
                }
                Assert.assertTrue("expected status code but is " + exchange.getStatusCodeValue(), integers.contains(exchange.getStatusCodeValue()));
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, threads).forEach(e -> {
            runnables.add(runnable);
            runnables.add(runnable2);
        });
        try {
            assertConcurrent("", runnables, 30000);
            // get product order count
            Thread.sleep(60*1000);
            ResponseEntity<ProductDetailAdminRepresentation> ex = action.readProductDetailByIdAdmin(productId);
            Assert.assertEquals(Math.max(iniOrderStorage.get(), 0), ex.getBody().getSkus().get(0).getStorageOrder().intValue());
            ResponseEntity<ProductDetailAdminRepresentation> ex2 = action.readProductDetailByIdAdmin(productId2);
            Assert.assertEquals(Math.max(iniOrderStorage2.get(), 0), ex2.getBody().getSkus().get(0).getStorageOrder().intValue());
            ResponseEntity<ProductDetailAdminRepresentation> ex3 = action.readProductDetailByIdAdmin(productId3);
            Assert.assertEquals(Math.max(iniOrderStorage3.get(), 0), ex3.getBody().getSkus().get(0).getStorageOrder().intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transactionalDecreaseNotEnough() throws InterruptedException {
        create_three_product_then_concurrent_decrease_diff_product_concurrent(50, 30);
    }

    @Test
    public void transactionalDecreaseEnough() throws InterruptedException {
        create_three_product_then_concurrent_decrease_diff_product_concurrent(100, 30);
    }

    @Test
    public void admin_update_product_while_product_total_sales_change_by_order_confirm() {
        ResponseEntity<String> exchange = action.createRandomProductDetail(100);
        String productId = exchange.getHeaders().getLocation().toString();
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("sum");
        patchCommand.setExpect(1);
        patchCommand.setValue(1);
        patchCommand.setPath("/" + productId + "/totalSales");
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        patchCommands.add(patchCommand);
        Integer threadCount = 50;
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(action.getJwtClientCredential(CLIENT_ID_SAGA_ID, COMMON_CLIENT_SECRET).getBody().getValue());
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(200);
        ArrayList<Integer> integers2 = new ArrayList<>();
        integers2.add(200);
        integers2.add(400);
        Runnable orderConfirm = new Runnable() {
            @Override
            public void run() {
                HttpEntity<ArrayList<PatchCommand>> listHttpEntity = new HttpEntity<>(patchCommands, headers2);
                ResponseEntity<Object> exchange2 = action.restTemplate.exchange(URL_2, HttpMethod.PATCH, listHttpEntity, Object.class);
                Assert.assertTrue("expected status code but is " + exchange2.getStatusCodeValue(), integers.contains(exchange2.getStatusCodeValue()));
            }
        };
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        UpdateProductAdminCommand command = new UpdateProductAdminCommand();
        UpdateProductAdminSkuCommand productSku = new UpdateProductAdminSkuCommand();
        productSku.setPrice(BigDecimal.valueOf(new Random().nextDouble()).abs());
        productSku.setAttributesSales(new HashSet<>(List.of(TEST_TEST_VALUE)));
        productSku.setVersion(0);
        command.setDescription(action.getRandomStr());
        command.setSkus(new ArrayList<>(List.of(productSku)));
        command.setStatus(ProductStatus.AVAILABLE);
        command.setName(action.getRandomStr());
        command.setImageUrlSmall("http://www.test.com/" + action.getRandomStr());
        Set<String> strings = new HashSet<>();
        strings.add(TEST_TEST_VALUE);
        command.setAttributesKey(strings);
        command.setVersion(0);
        Runnable adminUpdate = () -> {
            String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + PRODUCTS_ADMIN + "/" + exchange.getHeaders().getLocation().toString();
            HttpEntity<UpdateProductAdminCommand> request2 = new HttpEntity<>(command, headers);
            ResponseEntity<String> exchange2 = action.restTemplate.exchange(url2, HttpMethod.PUT, request2, String.class);
            Assert.assertTrue("expected status code in admin update but is " + exchange2.getStatusCodeValue(), integers2.contains(exchange2.getStatusCodeValue()));
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, threadCount).forEach(e -> {
            runnables.add(orderConfirm);
            runnables.add(adminUpdate);
        });
        try {
            assertConcurrent("", runnables, 30000);
            Thread.sleep(10000);
            // get product order count
            ResponseEntity<ProductDetailAdminRepresentation> productDetailAdminRepresentationResponseEntity = action.readProductDetailByIdAdmin(productId);
            assertTrue("total sales should be " + threadCount + " but is " + productDetailAdminRepresentationResponseEntity.getBody().getTotalSales(), productDetailAdminRepresentationResponseEntity.getBody().getTotalSales().equals(threadCount));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void customer_read_product_while_it_taken_down_by_admin() {
        AtomicBoolean emptyBody = new AtomicBoolean(false);
        ResponseEntity<String> exchange = action.createRandomProductDetail(null);
        String pId = exchange.getHeaders().getLocation().toString();
        //customer can read product
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + PRODUCTS_PUBLIC + "/" + pId;
        ResponseEntity<ProductDetail> exchange1 = action.restTemplate.exchange(url2, HttpMethod.GET, null, ProductDetail.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        //admin take down product
        Runnable runnable1 = () -> {
            HttpHeaders headers = new HttpHeaders();
            String s1 = action.getDefaultAdminToken();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(s1);
            UpdateProductAdminCommand command = new UpdateProductAdminCommand();
            command.setName(action.getRandomStr());
            command.setEndAt(System.currentTimeMillis());
            command.setImageUrlSmall("http://www.test.com/" + action.getRandomStr());
            Set<String> strings = new HashSet<>();
            strings.add(TEST_TEST_VALUE);
            command.setAttributesKey(strings);
            command.setVersion(0);
            UpdateProductAdminSkuCommand skuCommand = new UpdateProductAdminSkuCommand();
            skuCommand.setPrice(BigDecimal.TEN);
            skuCommand.setAttributesSales(new HashSet<>(List.of(TEST_TEST_VALUE)));
            skuCommand.setVersion(0);
            ArrayList<UpdateProductAdminSkuCommand> skuCommands = new ArrayList<>();
            skuCommands.add(skuCommand);
            command.setSkus(skuCommands);
            String url3 = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + PRODUCTS_ADMIN + "/" + pId;
            HttpEntity<UpdateProductAdminCommand> request2 = new HttpEntity<>(command, headers);
            ResponseEntity<String> exchange2 = action.restTemplate.exchange(url3, HttpMethod.PUT, request2, String.class);
            Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        };
        Runnable runnable2 = () -> {
            //customer read again
            ResponseEntity<ProductDetail> exchange3 = action.restTemplate.exchange(url2, HttpMethod.GET, null, ProductDetail.class);
            Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
            emptyBody.compareAndExchange(false, true);
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable1);
        runnables.add(runnable2);
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            assertConcurrent("", runnables, 30000);
            assertTrue(emptyBody.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}