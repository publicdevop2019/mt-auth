package com.mt.common.application.instance;

import com.mt.common.domain.model.instance.Instance;
import lombok.Data;

@Data
public class InstanceRepresentation {
    private Integer id;

    public InstanceRepresentation(Instance instance) {
        this.id = instance.getId();
    }
}
