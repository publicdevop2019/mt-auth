package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.SumPagedRep;
import com.mt.proxy.domain.exception.DefinedRuntimeException;
import com.mt.proxy.domain.exception.HttpResponseCode;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class HttpUtility {
    @Getter
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;
    @Getter
    @Value("${mt.misc.url.access}")
    private String accessUrl;

    /**
     * load all paginated data
     *
     * @param urlWithoutPagination url
     * @param pageSize             page size
     * @param allowEmpty           allow empty result
     * @param <T>                  type T
     * @return unique <T> set
     */
    public <T> Set<T> loadAllData(String urlWithoutPagination, int pageSize, boolean allowEmpty,
                                  ParameterizedTypeReference<SumPagedRep<T>> parameterizedTypeReference) {
        Set<T> data;
        ResponseEntity<SumPagedRep<T>> exchange = getRestTemplate()
            .exchange(urlWithoutPagination + "?page=size:" + pageSize + ",num:0", HttpMethod.GET,
                null,
                parameterizedTypeReference);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            SumPagedRep<T> body = exchange.getBody();
            if (body == null) {
                log.error("unable to load data from remote: " + urlWithoutPagination);
                throw new DefinedRuntimeException("unable to load data from remote address", "2009",
                    HttpResponseCode.INTERNAL_SERVER_ERROR);
            }
            if (body.getData().size() == 0) {
                if (allowEmpty) {
                    return Collections.emptySet();
                }
                log.error("data from remote: " + urlWithoutPagination + " is empty");
                throw new DefinedRuntimeException("unable to load data from remote address", "2009",
                    HttpResponseCode.INTERNAL_SERVER_ERROR);
            }
            data = new LinkedHashSet<>(body.getData());
            double l = (double) body.getTotalItemCount() / body.getData().size();
            double ceil = Math.ceil(l);
            int i = BigDecimal.valueOf(ceil).intValue();
            for (int a = 1; a < i; a++) {
                ResponseEntity<SumPagedRep<T>> exchange2 = getRestTemplate()
                    .exchange(urlWithoutPagination + "?page=size:" + pageSize + ",num:" + a,
                        HttpMethod.GET, null,
                        parameterizedTypeReference);
                SumPagedRep<T> body2 = exchange2.getBody();
                if (body2 == null || body2.getData().size() == 0) {
                    throw new DefinedRuntimeException("unable to load data from remote address",
                        "2009",
                        HttpResponseCode.INTERNAL_SERVER_ERROR);
                }
                data.addAll(body2.getData());
            }
        } else {
            log.error("error during load data {}", exchange.getBody());
            throw new DefinedRuntimeException("unable to load data from remote address", "2009",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
        return data;
    }
}
