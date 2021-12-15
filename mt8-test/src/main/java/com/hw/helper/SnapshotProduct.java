package com.hw.helper;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SnapshotProduct {

    private String name;
    private String id;

    private List<ProductOption> selectedOptions;

    private String finalPrice;

    private String imageUrlSmall;

    private String productId;
    private String skuId;
    private String cartId;
    private Integer amount;
    private Integer version;

    private Set<String> attributesSales;

}
