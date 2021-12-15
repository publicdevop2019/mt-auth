package com.mt.common.domain.model.domain_event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * clear thread local
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class DomainEventFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        DomainEventPublisher.instance().reset();
        chain.doFilter(req, res);
    }
}