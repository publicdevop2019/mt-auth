package com.mt.common.domain.model.develop;

import com.mt.common.infrastructure.thread_pool.CleanUpThreadPoolExecutor;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.io.File;
import java.util.concurrent.Executor;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Autowired
    private ServletWebServerApplicationContext serverApplicationContext;

    @Bean
    public PrometheusMeterRegistry createRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public ApplicationRunner monitorJvm(MeterRegistry meterRegistry) {
        return args -> {
            new JvmMemoryMetrics().bindTo(meterRegistry);
            new JvmGcMetrics().bindTo(meterRegistry);
            new JvmThreadMetrics().bindTo(meterRegistry);
        };
    }

    @Bean
    public ApplicationRunner monitorSystem(MeterRegistry meterRegistry) {
        return args -> {
            new DiskSpaceMetrics(new File("/")).bindTo(meterRegistry);
            new ProcessorMetrics().bindTo(meterRegistry);
        };
    }

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
    public ApplicationRunner monitorHikari(HikariDataSource dataSource,
                                           MeterRegistry meterRegistry) {
        return args -> {
            dataSource.setMetricRegistry(meterRegistry);
        };
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

    @Bean
    public ApplicationRunner monitorTomcat(MeterRegistry meterRegistry) {
        return args -> {
            TomcatWebServer tomcatWebServer =
                (TomcatWebServer) serverApplicationContext.getWebServer();
            Connector connector = tomcatWebServer.getTomcat().getConnector();
            Executor executor = connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                Gauge.builder("tomcat.threads.current", threadPoolExecutor,
                        ThreadPoolExecutor::getPoolSize)
                    .register(meterRegistry);

                Gauge.builder("tomcat.threads.active", threadPoolExecutor,
                        ThreadPoolExecutor::getActiveCount)
                    .register(meterRegistry);

                Gauge.builder("tomcat.threads.max", threadPoolExecutor,
                        ThreadPoolExecutor::getMaximumPoolSize)
                    .register(meterRegistry);
            }
        };
    }

}
