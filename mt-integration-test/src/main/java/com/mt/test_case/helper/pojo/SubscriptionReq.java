package com.mt.test_case.helper.pojo;

import lombok.Data;

@Data
public class SubscriptionReq {
    private String id;
    private String endpointId;
    private String projectId;
    private Integer burstCapacity;
    private Integer replenishRate;
}
