package com.hw.helper;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreateProductAdminCommand {
    private String name;
    private String imageUrlSmall;
    private Set<String> imageUrlLarge;
    private String description;
    private List<ProductOption> selectedOptions;
    private Set<String> specification;
    private Set<String> attributesKey;
    private Set<String> attributesProd;
    private Set<String> attributesGen;
    private List<ProductSku> skus;
    private ProductStatus status;
    private Long startAt;
}
