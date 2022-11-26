package com.mt.proxy.domain.rate_limit;

import com.mt.proxy.domain.Endpoint;
import java.util.Objects;
import lombok.Data;

@Data
public class Subscription {
    private String endpointId;
    private String projectId;
    private int replenishRate;
    private int burstCapacity;

    /**
     * create subscription for endpoint's project
     * @param ep endpoint
     */
    public Subscription(Endpoint ep) {
        this.endpointId = ep.getId();
        this.projectId = ep.getProjectId();
        this.burstCapacity = ep.getBurstCapacity();
        this.replenishRate = ep.getReplenishRate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subscription that = (Subscription) o;
        return Objects.equals(endpointId, that.endpointId) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(replenishRate, that.replenishRate) &&
            Objects.equals(burstCapacity, that.burstCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpointId, projectId, replenishRate, burstCapacity);
    }
}
