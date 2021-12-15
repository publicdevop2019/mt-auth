package com.mt.common.domain.model.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


//set UUID & CLIENT_IP for logging purpose,
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String uuid = httpRequest.getHeader("UUID");
        if (null == uuid) {
            MDC.put("UUID", UUID.randomUUID().toString());
        } else {
            MDC.put("UUID", uuid);
        }
        String ipFromHeader = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            MDC.put("CLIENT_IP", ipFromHeader);
        } else {
            MDC.put("CLIENT_IP", servletRequest.getRemoteAddr());
        }
        filterChain.doFilter(servletRequest, servletResponse);
        httpResponse.setHeader("UUID", MDC.get("UUID"));
    }
}
