package com.hw.helper;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateBizOrderCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String orderId;
    private Boolean paymentStatus;
    private String orderState;
    private Integer version;
    private String changeId;
}
