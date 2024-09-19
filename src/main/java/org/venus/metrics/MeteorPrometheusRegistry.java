package org.venus.metrics;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.venus.support.VenusException;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * The MeteorPrometheusRegistry class implements the MetricsRegistrySetUp interface to set up
 * and manage a Prometheus MeterRegistry. This class starts an HTTP server to expose
 * Prometheus metrics for scraping.
 *
 * The server listens on the host and port configured in MetricsProperties and
 * provides metrics at the specified scrape URL.
 *
 * It uses a virtual thread to start the server asynchronously.
 *
 * If the metrics is enabled through the MetricsProperties, the server will start and
 * the PrometheusMeterRegistry will be added to the global Metrics registry.
 *
 * The shutdown method stops the HTTP server when called.
 */
@Slf4j
public class MeteorPrometheusRegistry implements MetricsRegistrySetUp {
    /**
     * The HTTP server instance used to expose Prometheus metrics for scraping.
     * It listens on the configured host and port, providing metrics at the specified scrape URL.
     * The server runs asynchronously using a virtual thread.
     */
    private HttpServer server;
    /**
     * A MeterRegistry instance used for recording and managing Prometheus metrics.
     *
     * This registry is initialized in the setUp method if metrics collection is enabled
     * through the MetricsProperties configuration. It is added to the global Metrics registry
     * to facilitate metrics collection and reporting.
     *
     * The registry is also used to respond to HTTP requests with scraped metrics data,
     * specifically set up in the exportHttpServer method.
     */
    private MeterRegistry registry;

    /**
     * Configuration properties for the Prometheus metrics server.
     *
     * This variable holds the settings for enabling the metrics,
     * the scrape URL, the host address, and the port number
     * for the metrics HTTP server. It is used while setting up
     * the Prometheus MeterRegistry and starting the HTTP server
     * for exposing metrics.
     */
    private final MetricsProperties properties;

    /**
     * Initializes a new instance of the MeteorPrometheusRegistry class using the
     * specified metrics properties.
     *
     * @param properties an instance of MetricsProperties containing the configuration
     *                   for the Prometheus metrics, such as host, port, and scrape URL.
     */
    public MeteorPrometheusRegistry(MetricsProperties properties) {
        this.properties = properties;
    }

    /**
     * Sets up the Prometheus MeterRegistry and starts the HTTP server for exposing metrics.
     *
     * If metrics are enabled in the configuration properties, this method initializes a
     * PrometheusMeterRegistry with default settings and adds it to the global Metrics registry.
     * Additionally, it calls the exportHttpServer method to start an HTTP server that listens
     * on the configured host and port, exposing metrics at the specified scrape URL.
     *
     * Throws VenusException if there is a failure in starting the HTTP server.
     */
    @Override
    public void setUp() {
        if (properties.isEnabled()) {
            this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            Metrics.addRegistry(this.registry);

            exportHttpServer(properties);
        }
    }

    /**
     * Starts an HTTP server to expose Prometheus metrics for scraping. Configures the server
     * based on the provided {@link MetricsProperties}.
     *
     * @param properties the properties containing host, port, and scrape URL configurations
     */
    private void exportHttpServer(MetricsProperties properties) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(properties.getHost(), properties.getPort());
            this.server = HttpServer.create(socketAddress, 0);

            String url = properties.getScrapeUrl();
            this.server.createContext(url, exchange -> {
                String scrape = ((PrometheusMeterRegistry) this.registry).scrape();
                exchange.sendResponseHeaders(HttpStatus.OK.value(),
                        scrape.getBytes(StandardCharsets.UTF_8).length);

                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(scrape.getBytes());
                }
            });

            Thread.ofVirtual().name("venus-prometheus-start-virtual").start(() -> {
                try {
                    this.server.start();
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Venus prometheus http server start failure", e);
                    }
                }
            });

            if (log.isInfoEnabled()) {
                log.info("Venus prometheus http server is listening at socket address[{}] and scrape url[{}]", socketAddress, url);
            }
        } catch (Throwable t) {
            throw new VenusException("Venus prometheus start http server failure", t);
        }
    }

    /**
     * Shuts down the HTTP server that was started to expose Prometheus metrics.
     *
     * This method stops the HTTP server if it is running, ensuring that no
     * further metrics will be exposed for scraping.
     */
    @Override
    public void shutdown() {
        if (server != null) {
            this.server.stop(0);
        }
    }
}
