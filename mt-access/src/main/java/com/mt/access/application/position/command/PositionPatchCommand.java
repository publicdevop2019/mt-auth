package com.mt.access.application.position.command;

import com.mt.access.domain.model.position.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PositionPatchCommand {
    private String name;

    public PositionPatchCommand(Position project) {
        this.name = project.getName();
    }
}
