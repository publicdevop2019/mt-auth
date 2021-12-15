package com.hw.helper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdateProductAdminSkuCommand {
    private Integer decreaseActualStorage;
    private Integer decreaseOrderStorage;
    private Integer increaseActualStorage;
    private Integer increaseOrderStorage;
    private Set<String> attributesSales;
    private BigDecimal price;
    private Integer storageOrder;
    private Integer storageActual;
    private Integer sales;
    private Integer version;
}
