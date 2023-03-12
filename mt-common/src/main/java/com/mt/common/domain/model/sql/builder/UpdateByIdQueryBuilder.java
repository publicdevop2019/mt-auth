package com.mt.common.domain.model.sql.builder;


import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;
import static com.mt.common.CommonConstant.PATCH_OP_TYPE_ADD;
import static com.mt.common.CommonConstant.PATCH_OP_TYPE_REMOVE;
import static com.mt.common.CommonConstant.PATCH_OP_TYPE_REPLACE;

import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.audit.NextAuditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.PatchCommand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public abstract class UpdateByIdQueryBuilder<T extends NextAuditable> extends UpdateQueryBuilder<T> {
    protected Map<String, String> filedMap = new HashMap<>();
    protected Map<String, Function<Object, ?>> filedTypeMap = new HashMap<>();

    @Override
    protected void setUpdateValue(Root<T> root, CriteriaUpdate<T> criteriaUpdate,
                                  PatchCommand command) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        filedMap.keySet().forEach(e -> {
            booleans.add(setUpdateValueFor(e, filedMap.get(e), criteriaUpdate, command));
        });
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new DefinedRuntimeException("no updatable field", "0031",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_STATE);
        }
    }

    private boolean setUpdateValueFor(String fieldPath, String fieldLiteral,
                                      CriteriaUpdate<T> criteriaUpdate, PatchCommand command) {
        if (command.getPath().contains(fieldPath)) {
            if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REMOVE)) {
                criteriaUpdate.set(fieldLiteral, null);
                return true;
            } else if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_ADD)
                ||
                command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REPLACE)) {
                if (command.getValue() != null) {
                    criteriaUpdate
                        .set(fieldLiteral, filedTypeMap.get(fieldPath).apply(command.getValue()));
                } else {
                    criteriaUpdate.set(fieldLiteral, null);
                }
                return true;
            } else {
                throw new DefinedRuntimeException("unsupported patch operation", "0032",
                    HttpResponseCode.BAD_REQUEST,
                    ExceptionCatalog.OPERATION_ERROR);
            }
        } else {
            return false;
        }
    }

    @Override
    public Predicate getWhereClause(Root<T> root, List<String> query, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : query) {
            Predicate equal = cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(str));
            results.add(equal);
        }
        return cb.or(results.toArray(new Predicate[0]));
    }


}
