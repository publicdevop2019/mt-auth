package com.mt.common.domain.model.logging;

import static com.mt.common.domain.model.constant.AppInfo.SPAN_ID_LOG;
import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.CommonDomainRegistry;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    public void initTrace() {
        MDC.clear();
        MDC.put(SPAN_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
        MDC.put(TRACE_ID_LOG, CommonDomainRegistry.getUniqueIdGeneratorService().idString());
    }
}
