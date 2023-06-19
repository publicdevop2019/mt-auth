package com.mt.access.domain.model.sub_request;


import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class SubRequestValidator {
    private final SubRequest subRequest;
    private final ValidationNotificationHandler handler;

    public SubRequestValidator(SubRequest subRequest, ValidationNotificationHandler handler) {
        this.subRequest = subRequest;
        this.handler = handler;
    }

    protected void validate() {
        replenishRateAndBurstCapacity();
    }

    private void replenishRateAndBurstCapacity() {
        if (subRequest.getReplenishRate() != 0 || subRequest.getBurstCapacity() != 0) {
            if (subRequest.getBurstCapacity() < subRequest.getReplenishRate()) {
                handler.handleError("replenish rate must less than or equal to burst capacity");
            }
        }
    }

}
