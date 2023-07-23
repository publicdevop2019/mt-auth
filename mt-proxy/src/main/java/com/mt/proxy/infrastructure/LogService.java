package com.mt.proxy.infrastructure;

import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.SPAN_ID_LOG;
import static com.mt.proxy.infrastructure.AppConstant.TRACE_ID_LOG;

import com.mt.proxy.domain.UniqueIdGeneratorService;
import com.mt.proxy.domain.Utility;
import java.util.function.Consumer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    UniqueIdGeneratorService idGeneratorService;

    public static void reactiveLog(ServerHttpRequest request,
                                   Consumer<Void> consumer) {
        String spanId = Utility.getSpanId(request);
        String traceId = Utility.getTraceId(request);
        String clientIp = Utility.getClientIp(request);
        MDC.put(SPAN_ID_LOG, spanId);
        MDC.put(TRACE_ID_LOG, traceId);
        MDC.put(REQ_CLIENT_IP, clientIp);
        consumer.accept(null);
        MDC.clear();
    }

    public void initTrace() {
        MDC.clear();
        MDC.put(SPAN_ID_LOG, idGeneratorService.idString());
        MDC.put(TRACE_ID_LOG, idGeneratorService.idString());
    }
}
