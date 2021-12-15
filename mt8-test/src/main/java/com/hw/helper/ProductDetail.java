package com.hw.helper;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductDetail extends ProductSimple {

    private String id;

    private String imageUrlSmall;

    private String name;

    private String description;

    private List<ProductOption> selectedOptions;

    private Set<String> imageUrlLarge;

    private Set<String> specification;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSku> productSkuList;

    private ProductStatus status;
    private Integer version;
}
