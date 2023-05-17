package com.mt.test_case.helper.pojo;

import lombok.Data;

@Data
public class SubscriptionReq {
    private String endpointId;
    private String projectId;
    private int burstCapacity;
    private int replenishRate;
}
