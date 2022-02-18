package com.mt.common.infrastructure.audit;

import com.mt.common.domain.model.jwt.JwtUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SpringDataJpaConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    public static class AuditorAwareImpl implements AuditorAware<String> {
        public static Optional<String> getAuditor() {
            Optional<HttpServletRequest> httpServletRequest = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                    .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                    .map(ServletRequestAttributes::getRequest);
            if (httpServletRequest.isEmpty())
                return Optional.of("NOT_HTTP");
            String authorization = httpServletRequest.get().getHeader("authorization");
            if (authorization == null)
                return Optional.ofNullable("EMPTY_AUTH_HEADER");
            return Optional.ofNullable(
                    JwtUtility.getUserId(authorization) == null ?
                            JwtUtility.getClientId(authorization) : JwtUtility.getUserId(authorization));
        }

        @Override
        public Optional<String> getCurrentAuditor() {
            Optional<HttpServletRequest> httpServletRequest = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                    .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                    .map(ServletRequestAttributes::getRequest);
            if (httpServletRequest.isEmpty())
                return Optional.of("NOT_HTTP");
            String authorization = httpServletRequest.get().getHeader("authorization");
            if (authorization == null)
                return Optional.ofNullable("EMPTY_AUTH_HEADER");
            if (authorization.contains("Basic"))
                return Optional.ofNullable("ONBOARD_TENANT_USER");
            return Optional.ofNullable(
                    JwtUtility.getUserId(authorization) == null ?
                            JwtUtility.getClientId(authorization) : JwtUtility.getUserId(authorization));
        }
    }
}
