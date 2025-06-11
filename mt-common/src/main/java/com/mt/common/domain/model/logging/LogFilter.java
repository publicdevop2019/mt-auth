package com.mt.common.domain.model.logging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.infrastructure.Utility;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * set trace id, span id and client ip for logging purpose.
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String traceId = httpRequest.getHeader(AppInfo.TRACE_ID_HTTP);
        if (null == traceId) {
            traceId = CommonDomainRegistry.getUniqueIdGeneratorService().idString();
        }
        MDC.put(AppInfo.TRACE_ID_LOG, traceId);

        String spanId = CommonDomainRegistry.getUniqueIdGeneratorService().idString();
        MDC.put(AppInfo.SPAN_ID_LOG, spanId);
        String clientIp = Utility.getClientIpAddress(httpRequest);
        log.info("http client ip {} url: {}", clientIp,
            ((HttpServletRequest) servletRequest).getRequestURI());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            //MDC is based on ThreadLocal, it has potential data leak & memory leak
            //problem, which requires to clear it after work finish including exceptions
            MDC.clear();
        }
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        if (httpResponse.getHeader(AppInfo.TRACE_ID_HTTP) == null) {
            httpResponse.setHeader(AppInfo.TRACE_ID_HTTP, traceId);
        }
    }
}
