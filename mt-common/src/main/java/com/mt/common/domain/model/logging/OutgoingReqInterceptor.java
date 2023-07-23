package com.mt.common.domain.model.logging;

import static com.mt.common.domain.model.constant.AppInfo.REQUEST_ID_HTTP;
import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_HTTP;
import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OutgoingReqInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution)
        throws IOException {

        String requestId = CommonDomainRegistry.getUniqueIdGeneratorService().idString();
        httpRequest.getHeaders().set(REQUEST_ID_HTTP, requestId);
        if (null == MDC.get(TRACE_ID_LOG)) {
            String traceId = CommonDomainRegistry.getUniqueIdGeneratorService().idString();
            MDC.put(TRACE_ID_LOG, traceId);
            log.info("trace id created {} path {}", traceId,
                httpRequest.getURI());
        }
        log.debug("request id created {} path {}", requestId, httpRequest.getURI());
        httpRequest.getHeaders().set(TRACE_ID_HTTP, MDC.get(TRACE_ID_LOG));
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
