package org.venus.metrics;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.http.HttpStatus;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class MeteorPrometheusRegistry implements MetricsRegistrySetUp {
    private HttpServer server;
    private MeterRegistry registry;

    private final MetricsProperties properties;

    public MeteorPrometheusRegistry(MetricsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setUp() {
        if (properties.isEnabled()) {
            this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            Metrics.addRegistry(this.registry);

            exportHttpServer(properties);
        }
    }

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
            new Thread(this.server::start).start();
            if (log.isInfoEnabled()) {
                log.info("Venus prometheus http server is listening at socket address[{}], and scrape url[{}]", socketAddress, url);
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Venus start prometheus http server failed", t);
            }
            throw new RuntimeException(t);
        }
    }

    @Override
    public void shutdown() {
        if (server != null) {
            this.server.stop(0);
        }
    }
}
