package com.hw.helper;

import lombok.Data;

import java.util.List;

@Data
public class CartProduct {

    private Long id;

    private String name;

    private List<ProductOption> selectedOptions;

    private String finalPrice;

    private String imageUrlSmall;

    private String productId;

    private OrderDetail customerOrder;

}
