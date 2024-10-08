package org.venus.metrics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for setting up Prometheus metrics in a Spring application.
 *
 * This class binds properties from the application configuration file to configure
 * the Prometheus metrics endpoint, host, and port.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.venus.metrics.prometheus")
public class MetricsProperties {
    /**
     * Indicates whether Prometheus metrics are enabled.
     *
     * This flag determines if the Prometheus metrics endpoint should be activated.
     * By default, it is set to true, enabling the metrics collection.
     */
    private boolean enabled = true;
    /**
     * The URL path for Prometheus to scrape metrics from.
     *
     * This is used to specify the endpoint where Prometheus can
     * collect the metrics exposed by the application.
     */
    private String scrapeUrl = "/prometheus";
    /**
     * The hostname or IP address where the Prometheus metrics endpoint is exposed.
     * Defaults to "127.0.0.1".
     */
    private String host = "127.0.0.1";
    /**
     * The port number on which the Prometheus metrics are exposed.
     */
    private int port = 9090;
    /**
     * The duration period for collecting cache metrics.
     *
     * This variable defines how frequently the cache metrics should be gathered.
     * It is represented as a Duration object, which provides utilities
     * for managing time-based values.
     */
    private Duration cacheMetricsPeriod;

    /**
     * Indicates whether cache metrics collection is enabled.
     *
     * This flag determines if cache-related metrics should be collected
     * and reported to Prometheus.
     * By default, it is set to true, enabling the collection of cache metrics.
     */
    private boolean cacheMetricsEnabled = true;
}
