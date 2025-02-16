package com.mt.common.domain.model.develop;

import com.mt.common.infrastructure.thread_pool.CleanUpThreadPoolExecutor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public FilterRegistrationBean<MetricsFilter> loggingFilter(
        MetricsFilter metricsFilter
    ) {
        FilterRegistrationBean<MetricsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(metricsFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Integer.MAX_VALUE);
        return registrationBean;
    }


    @Bean
    public ApplicationRunner monitorCustomThreadPool(
        @Qualifier("job") CleanUpThreadPoolExecutor job,
        @Qualifier("event-sub") CleanUpThreadPoolExecutor eventSub,
        @Qualifier("event-pub") CleanUpThreadPoolExecutor eventPub,
        @Qualifier("event-exe") CleanUpThreadPoolExecutor eventExe,
        @Qualifier("event-mark") CleanUpThreadPoolExecutor markEvent,
        MeterRegistry meterRegistry
    ) {
        return args -> {
            new ExecutorServiceMetrics(job, "job", null).bindTo(meterRegistry);
            new ExecutorServiceMetrics(job, "event-sub", null).bindTo(meterRegistry);
            new ExecutorServiceMetrics(job, "event-pub", null).bindTo(meterRegistry);
            new ExecutorServiceMetrics(job, "event-exe", null).bindTo(meterRegistry);
            new ExecutorServiceMetrics(job, "event-mark", null).bindTo(meterRegistry);
        };
    }


}
