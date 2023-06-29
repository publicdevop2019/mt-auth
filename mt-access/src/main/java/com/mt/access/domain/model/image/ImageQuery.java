package com.mt.access.domain.model.image;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@ToString
public class ImageQuery extends QueryCriteria {
    @Getter
    private final Set<ImageId> ids;
    @Getter
    private final Sort sort;

    public ImageQuery(ImageId id) {
        this.ids = Collections.singleton(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    @Getter
    public static class Sort {
        private final Boolean byId = true;
        private final Boolean isAsc;

        private Sort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static Sort byId(boolean isAsc) {
            return new Sort(isAsc);
        }
    }
}
