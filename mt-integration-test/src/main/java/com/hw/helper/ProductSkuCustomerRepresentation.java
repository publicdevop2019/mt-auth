package com.hw.helper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductSkuCustomerRepresentation {
    private Set<String> attributesSales;
    private Integer storage;
    private BigDecimal price;
    private String skuId;
}
