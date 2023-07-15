package com.mt.access.application.position;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.position.command.PositionCreateCommand;
import com.mt.access.application.position.command.PositionPatchCommand;
import com.mt.access.application.position.command.PositionUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.position.Position;
import com.mt.access.domain.model.position.PositionId;
import com.mt.access.domain.model.position.PositionQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PositionApplicationService {

    private static final String PERMISSION = "Position";

    public SumPagedRep<Position> tenantQuery(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getPositionRepository()
            .query(new PositionQuery(queryParam, pageParam, skipCount));
    }

    public Position tenantQuery(String id) {
        return DomainRegistry.getPositionRepository().get(new PositionId(id));
    }


    public void tenantUpdate(String id, PositionUpdateCommand command, String changeId) {
        PositionId positionId = new PositionId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Position> first =
                    DomainRegistry.getPositionRepository().query(new PositionQuery(positionId))
                        .findFirst();
                first.ifPresent(e -> {
                    e.replace(command.getName());
                    DomainRegistry.getPositionRepository().add(e);
                });
                return null;
            }, PERMISSION);
    }


    public void tenantRemove(String id, String changeId) {
        PositionId positionId = new PositionId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            Position position =
                DomainRegistry.getPositionRepository().get(positionId);
                DomainRegistry.getPositionRepository().remove(position);
            return null;
        }, PERMISSION);
    }


    public void tenantPatch(String id, JsonPatch command, String changeId) {
        PositionId positionId = new PositionId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Position position =
                    DomainRegistry.getPositionRepository().get(positionId);
                    PositionPatchCommand beforePatch = new PositionPatchCommand(position);
                    PositionPatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, PositionPatchCommand.class);
                    position.replace(
                        afterPatch.getName()
                    );
                return null;
            }, PERMISSION);
    }


    public String tenantCreate(PositionCreateCommand command, String changeId) {
        PositionId positionId = new PositionId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Position position = new Position(positionId, command.getName());
                DomainRegistry.getPositionRepository().add(position);
                return positionId.getDomainId();
            }, PERMISSION);
    }
}
