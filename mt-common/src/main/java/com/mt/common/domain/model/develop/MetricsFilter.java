package com.mt.common.domain.model.develop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    private static final String DOMAIN_PREFIX_REGEX = "^[0-9][A-Z][0-9A-Z]{10}$";
    @Autowired
    private MeterRegistry meterRegistry;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
        Timer.Sample sample = Timer.start(meterRegistry);
        filterChain.doFilter(request, response);
        sample.stop(meterRegistry.timer("http_server_requests",
            "method", request.getMethod(),
            "status", Integer.toString(response.getStatus()),
            "uri", normalize(request.getRequestURI())));
    }

    private String normalize(String requestURI) {
        String[] split = requestURI.split("/");
        List<String> collect = Arrays.stream(split).map(s -> {
            if (s.matches(DOMAIN_PREFIX_REGEX)) {
                return "**";
            } else {
                return s;
            }
        }).collect(Collectors.toList());
        return String.join("/", collect);
    }

}