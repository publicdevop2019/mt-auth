package com.hw.helper;

import lombok.Data;

import java.util.Set;

@Data
public class Catalog {

    private Long id;

    private String name;

    private String parentId;
    private Integer version;

    private Set<String> attributes;

    private CatalogType catalogType;
}
