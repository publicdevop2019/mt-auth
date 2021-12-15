package com.hw.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductSku {
    private Set<String> attributesSales;
    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;
    private Integer version;

}
