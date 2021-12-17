package com.mt.access.port.adapter.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * handles  RequestRejectedException which is not handled by controller advice
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CustomExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (RequestRejectedException ex) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}