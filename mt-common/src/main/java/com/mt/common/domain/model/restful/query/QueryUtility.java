package com.mt.common.domain.model.restful.query;

import com.mt.common.domain.model.develop.Analytics;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * utility class which provide support for jpa criteria api support,
 * use this when you deal with queries like "select * from table where order by"
 */
@Slf4j
@Component
public class QueryUtility {

    public static <T, S extends QueryCriteria> Set<T> getAllByQuery(
        Function<S, SumPagedRep<T>> ofQuery, S query) {
        SumPagedRep<T> sumPagedRep = ofQuery.apply(query);
        if (sumPagedRep.getData().size() == 0) {
            return Collections.emptySet();
        }
        //for accuracy
        double l =
            (double) sumPagedRep.getTotalItemCount() / sumPagedRep.getData().size();
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        Set<T> data = new LinkedHashSet<>(sumPagedRep.getData());
        Analytics queryAll = Analytics.start(Analytics.Type.DATA_QUERY_ALL);
        for (int a = 1; a < i; a++) {
            Analytics start = Analytics.start(Analytics.Type.DATA_QUERY);
            List<T> nextPage = ofQuery.apply(query.pageOf(a)).getData();
            start.stop();
            data.addAll(nextPage);
        }
        queryAll.stop();
        return data;
    }

    /**
     * parse give raw query string, throw {@link DefinedRuntimeException} if string is not key value pair and {@link DefinedRuntimeException} if keys are not found
     *
     * @param rawQuery        raw query string e.g. "key=value,key2=value2"
     * @param supportedFields allowed keys
     * @return parsed string map
     */
    public static Map<String, String> parseQuery(String rawQuery, String... supportedFields) {
        Map<String, String> stringStringMap = Optional.ofNullable(rawQuery).map(e -> {
            Map<String, String> parsed = new HashMap<>();
            String[] split = rawQuery.split(",");
            for (String str : split) {
                String[] split1 = str.split(":");
                if (split1.length != 2) {
                    throw new DefinedRuntimeException("unable to parse query string" + rawQuery,
                        "0027",
                        HttpResponseCode.BAD_REQUEST);
                }
                parsed.put(split1[0], split1[1]);
            }
            return parsed;
        }).orElseGet(Collections::emptyMap);
        validateQuery(stringStringMap, supportedFields);
        return stringStringMap;
    }

    private static void validateQuery(Map<String, String> parsedMap, String... supportedFields) {
        List<String> list = List.of(supportedFields);
        if (parsedMap.keySet().stream().anyMatch(e -> !list.contains(e))) {
            throw new DefinedRuntimeException("unknown query key", "0028",
                HttpResponseCode.BAD_REQUEST);
        }
    }
}
