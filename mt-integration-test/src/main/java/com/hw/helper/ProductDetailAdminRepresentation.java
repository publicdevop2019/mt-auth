package com.hw.helper;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductDetailAdminRepresentation {
    private String id;

    private String name;

    private String imageUrlSmall;

    private Set<String> imageUrlLarge;

    private String description;

    private Set<String> specification;

    private List<ProductOption> selectedOptions;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSku> skus;
    private Integer totalSales;
    private Integer version;
}
