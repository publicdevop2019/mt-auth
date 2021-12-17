package com.hw.helper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetail {
    private String id;
    private SnapshotAddress address;
    private List<SnapshotProduct> productList;
    private PaymentType paymentType;
    private String orderState;
    private BigDecimal paymentAmt;
    private String paymentDate;
    private PaymentStatus paymentStatus;
    private Date modifiedByUserAt;
    private Boolean expired;
    private Boolean revoked;
    private Boolean paid;

}

