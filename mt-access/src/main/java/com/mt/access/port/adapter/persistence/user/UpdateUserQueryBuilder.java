package com.mt.access.port.adapter.persistence.user;


import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.User_;
import com.mt.common.CommonConstant;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.sql.builder.UpdateByIdQueryBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserQueryBuilder extends UpdateByIdQueryBuilder<User> {
    {
        filedMap.put(User_.LOCKED, User_.LOCKED);
        filedTypeMap.put(User_.LOCKED, this::parseBoolean);
    }

    @Override
    public Predicate getWhereClause(Root<User> root, List<String> query, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : query) {
            Predicate equal = cb.equal(root.get(User_.USER_ID).get(CommonConstant.DOMAIN_ID), str);
            results.add(equal);
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Boolean parseBoolean(@Nullable Object input) {
        if (input == null) {
            throw new DefinedRuntimeException("unable parse boolean", "0074",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        if (input.getClass().equals(Boolean.class)) {
            return ((Boolean) input);
        }
        return Boolean.parseBoolean((String) input);
    }

}
