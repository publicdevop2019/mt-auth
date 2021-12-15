package com.hw.helper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ProductDetailCustomRepresentation {
    private String id;
    private String name;
    private String imageUrlSmall;
    private Set<String> imageUrlLarge;
    private String description;
    private Set<String> specification;
    private BigDecimal lowestPrice;
    private Integer totalSales;
    private List<ProductSkuCustomerRepresentation> skus;
    private List<ProductOption> selectedOptions;
}
