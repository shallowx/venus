package org.venus.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsPrometheusAutoConfiguration {
    @ConditionalOnClass(MeterRegistry.class)
    @Bean(initMethod = "setUp", destroyMethod = "shutdown")
    public MeteorPrometheusRegistry metricsRegistrySetUp(MetricsProperties properties) {
        return new MeteorPrometheusRegistry(properties);
    }

    @ConditionalOnClass(MeterRegistry.class)
    @Bean(initMethod = "setUp", destroyMethod = "shutdown")
    public JmxMetricsRegistry jmxMetricsRegistry() {
        return new JmxMetricsRegistry();
    }

    @ConditionalOnClass(MeterRegistry.class)
    @Bean
    public DefaultJmxMetrics defaultJmxMetrics() {
        return new DefaultJmxMetrics();
    }

}
