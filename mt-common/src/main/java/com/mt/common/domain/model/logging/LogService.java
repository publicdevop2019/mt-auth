package com.mt.common.domain.model.logging;

import static com.mt.common.domain.model.constant.AppInfo.SPAN_ID_LOG;
import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.CommonDomainRegistry;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    public void init() {
        MDC.clear();
        MDC.put(TRACE_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
        MDC.put(SPAN_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
    }

    public void initIfAbsent() {
        if (MDC.get(TRACE_ID_LOG) == null) {
            MDC.put(TRACE_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
        }
        if (MDC.get(SPAN_ID_LOG) == null) {
            MDC.put(SPAN_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
        }
    }

    public void initSpanId() {
        MDC.put(SPAN_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
    }

    public String getTraceId() {
        return MDC.get(TRACE_ID_LOG);
    }

    public void setTraceId(String traceId) {
        MDC.put(TRACE_ID_LOG, traceId);
    }
}
