package com.hw.integration.mall;

import com.hw.helper.ProductCustomerSummaryPaginatedRepresentation;
import com.hw.helper.UserAction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductQueryTest {
    @Autowired
    UserAction action;

    @Test
    public void search_product_by_tag() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装$" + s1 + "-女&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(3L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);
        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(1, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
    }

    @Test
    public void search_product_by_tag2() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(3L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        String s3 = productDetailForCatalog２.getHeaders().getLocation().toString();
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);

        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(2, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
        Assert.assertEquals(s3, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().get(0).getId().toString());
    }

    @Test
    public void search_product_by_tag3() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装$" + s1 + "-女&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(3L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);

        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(1, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
    }

    @Test
    public void search_product_by_tag4() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装$" + s1 + "-女&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(3L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);

        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(1, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
    }

    @Test
    public void search_product_by_tag5() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装$" + s1 + "-女.男&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(9L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);

        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(2, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
        String s3 = productDetailForCatalog.getHeaders().getLocation().toString();
        Assert.assertEquals(s3, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().get(0).getId().toString());
    }

    @Test
    public void search_product_by_tag6() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装.首饰$" + s1 + "-女.男" + s2 + "-L" + "&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(3L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);

        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(1, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
    }
    @Test
    public void search_product_by_tag7() {
        HashSet<String> strings = new HashSet<>();
        String s = UUID.randomUUID().toString().replace("-", "");
        String s1 = UUID.randomUUID().toString().replace("-", "");
        String s2 = UUID.randomUUID().toString().replace("-", "");
        String query = "query=attr:" + s + "-服装.首饰$" + s1 + "-女.男" + "&page=num:0,size:20,by:lowestPrice,order:asc";
        strings.add(s + ":服装");
        strings.add(s1 + ":女");
        strings.add(s2 + ":S");
        strings.add(s2 + ":M");
        strings.add(s2 + ":L");
        BigDecimal bigDecimal = BigDecimal.valueOf(3L);
        ResponseEntity<String> productDetailForCatalog = action.createRandomProductDetail(strings, null,bigDecimal);
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(s + ":服装");
        strings2.add(s1 + ":男");
        strings2.add(s2 + ":S");
        strings2.add(s2 + ":M");
        BigDecimal bigDecimal2 = BigDecimal.valueOf(1L);
        ResponseEntity<String> productDetailForCatalog２ = action.createRandomProductDetail(strings2, null,bigDecimal2);
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(s + ":首饰");
        strings3.add(s1 + ":男");
        strings3.add(s2 + ":S");
        strings3.add(s2 + ":M");
        BigDecimal bigDecimal3 = BigDecimal.valueOf(2L);
        ResponseEntity<String> productDetailForCatalog3 = action.createRandomProductDetail(strings3, null,bigDecimal3);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productCustomerSummaryPaginatedRepresentationResponseEntity = action.readProductsByQuery(query);

        Assert.assertEquals(HttpStatus.OK, productCustomerSummaryPaginatedRepresentationResponseEntity.getStatusCode());
        Assert.assertEquals(3, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().size());
        String s3 = productDetailForCatalog２.getHeaders().getLocation().toString();
        Assert.assertEquals(s3, productCustomerSummaryPaginatedRepresentationResponseEntity.getBody().getData().get(0).getId().toString());
    }
}
