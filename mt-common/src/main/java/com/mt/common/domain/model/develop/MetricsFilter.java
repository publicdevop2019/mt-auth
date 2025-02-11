package com.mt.common.domain.model.develop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class MetricsFilter extends OncePerRequestFilter {
    @Autowired
    private MeterRegistry meterRegistry;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
        Timer.Sample sample = Timer.start(meterRegistry);
        filterChain.doFilter(request, response);
        sample.stop(meterRegistry.timer("http_inbound_request",
            "method", request.getMethod(),
            "status", Integer.toString(response.getStatus()),
            "uri", request.getRequestURI()));
    }

}