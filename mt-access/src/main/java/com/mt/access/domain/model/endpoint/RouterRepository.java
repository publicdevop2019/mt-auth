package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface RouterRepository {

    default Router get(RouterId routerId) {
        Router router = query(routerId);
        Validator.notNull(router);
        return router;
    }

    default Router get(ProjectId projectId, RouterId routerId) {
        RouterQuery query =
            new RouterQuery(routerId, projectId);
        Router router = query(query).findFirst().orElse(null);
        Validator.notNull(router);
        return router;
    }

    Router query(RouterId routerId);

    void update(Router old, Router update);

    void add(Router router);

    void remove(Router router);

    SumPagedRep<Router> query(RouterQuery query);

}
