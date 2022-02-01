package com.mt.access.application.position.representation;

import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.position.Position;
import lombok.Data;

@Data
public class PositionCardRepresentation {
    private final String name;

    public PositionCardRepresentation(Position role) {
        this.name = role.getName();
    }
}
