package com.hw.helper;

import lombok.Data;

@Data
public class SubscriptionReq {
    private String endpointId;
    private String projectId;
    private int burstCapacity;
    private int replenishRate;
}
