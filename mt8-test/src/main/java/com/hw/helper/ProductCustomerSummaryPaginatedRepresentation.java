package com.hw.helper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCustomerSummaryPaginatedRepresentation {
    private List<ProductSearchRepresentation> data;
    private Integer totalPageCount;
    private Long totalProductCount;

    @Data
    public static class ProductSearchRepresentation {
        private String id;
        private String name;
        private String imageUrlSmall;
        private String description;
        private BigDecimal lowestPrice;
        private Integer totalSales;
    }
}
