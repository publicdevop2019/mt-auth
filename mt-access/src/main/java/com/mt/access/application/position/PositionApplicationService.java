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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PositionApplicationService {

    private static final String PERMISSION = "Position";

    public SumPagedRep<Position> query(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getPositionRepository()
            .getByQuery(new PositionQuery(queryParam, pageParam, skipCount));
    }

    public Optional<Position> getById(String id) {
        return DomainRegistry.getPositionRepository().getById(new PositionId(id));
    }


    @Transactional
    public void replace(String id, PositionUpdateCommand command, String changeId) {
        PositionId positionId = new PositionId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Position> first =
                    DomainRegistry.getPositionRepository().getByQuery(new PositionQuery(positionId))
                        .findFirst();
                first.ifPresent(e -> {
                    e.replace(command.getName());
                    DomainRegistry.getPositionRepository().add(e);
                });
                return null;
            }, PERMISSION);
    }


    @Transactional
    public void remove(String id, String changeId) {
        PositionId positionId = new PositionId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Position> corsProfile =
                DomainRegistry.getPositionRepository().getById(positionId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getPositionRepository().remove(e);
            });
            return null;
        }, PERMISSION);
    }


    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        PositionId positionId = new PositionId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<Position> corsProfile =
                    DomainRegistry.getPositionRepository().getById(positionId);
                if (corsProfile.isPresent()) {
                    Position corsProfile1 = corsProfile.get();
                    PositionPatchCommand beforePatch = new PositionPatchCommand(corsProfile1);
                    PositionPatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, PositionPatchCommand.class);
                    corsProfile1.replace(
                        afterPatch.getName()
                    );
                }
                return null;
            }, PERMISSION);
    }


    @Transactional
    public String create(PositionCreateCommand command, String changeId) {
        PositionId positionId = new PositionId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Position position = new Position(positionId, command.getName());
                DomainRegistry.getPositionRepository().add(position);
                return positionId.getDomainId();
            }, PERMISSION);
    }
}
