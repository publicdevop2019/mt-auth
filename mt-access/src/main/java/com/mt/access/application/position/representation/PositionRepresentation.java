package com.mt.access.application.position.representation;

import com.mt.access.domain.model.position.Position;
import lombok.Data;

@Data
public class PositionRepresentation {
    private String name;
    public PositionRepresentation(Position role) {
        this.name= role.getName();
    }
}
