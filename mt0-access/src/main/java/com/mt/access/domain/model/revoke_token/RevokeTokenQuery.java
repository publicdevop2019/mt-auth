package com.mt.access.domain.model.revoke_token;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RevokeTokenQuery extends QueryCriteria {
    public static final String TARGET_ID = "targetId";
    private Set<RevokeTokenId> revokeTokenId;

    public RevokeTokenQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,TARGET_ID);
        Optional.ofNullable(stringStringMap.get(TARGET_ID)).ifPresent(e -> revokeTokenId = Arrays.stream(e.split("\\$")).map(RevokeTokenId::new).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 2000));
        setQueryConfig(new QueryConfig(config));
    }
}
