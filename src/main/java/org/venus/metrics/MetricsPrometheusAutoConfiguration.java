package org.venus.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MetricsPrometheusAutoConfiguration is a configuration class that sets up
 * metrics collection and reporting using Prometheus and JMX registries.
 *
 * This class loads configuration properties for metrics from the application
 * properties file and initializes beans for the Prometheus and JMX metrics registries.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsPrometheusAutoConfiguration {
    /**
     * Sets up the Prometheus metrics registry using the provided metrics properties.
     *
     * This method initializes a new instance of MeteorPrometheusRegistry with the given
     * MetricsProperties, enabling the setup and management of Prometheus metrics.
     * The registry will be configured, started, and added to the global metrics registry.
     *
     * @param properties the metrics properties containing configurations for setting
     *                   up the Prometheus metrics, such as host, port, and scrape URL.
     * @return a configured instance of MeteorPrometheusRegistry.
     */
    @ConditionalOnClass(MeterRegistry.class)
    @Bean(initMethod = "setUp", destroyMethod = "shutdown")
    public MeteorPrometheusRegistry metricsRegistrySetUp(MetricsProperties properties) {
        return new MeteorPrometheusRegistry(properties);
    }

    /**
     * Creates and returns a new instance of JmxMetricsRegistry.
     *
     * @return a new JmxMetricsRegistry instance that sets up and manages
     *         a JMX MeterRegistry for collecting and reporting JMX metrics.
     */
    @ConditionalOnClass(MeterRegistry.class)
    @Bean(initMethod = "setUp", destroyMethod = "shutdown")
    public JmxMetricsRegistry jmxMetricsRegistry() {
        return new JmxMetricsRegistry();
    }

    /**
     * Bean definition for creating an instance of DefaultJmxMetrics.
     * This method is conditional on the presence of the MeterRegistry class in the classpath.
     *
     * @return a new instance of DefaultJmxMetrics that initializes and registers JVM-related metrics.
     */
    @ConditionalOnClass(MeterRegistry.class)
    @Bean
    public DefaultJmxMetrics defaultJmxMetrics() {
        return new DefaultJmxMetrics();
    }

}
