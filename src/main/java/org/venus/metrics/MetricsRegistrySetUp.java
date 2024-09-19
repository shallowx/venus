package org.venus.metrics;

/**
 * Interface for setting up and shutting down a metrics registry.
 */
public interface MetricsRegistrySetUp {
    /**
     * Sets up the metrics registry.
     *
     * Initializes necessary components or configurations required for the
     * metrics registry to function correctly. Depending on the implementation,
     * this may involve setting up HTTP servers, initializing registries, or
     * applying specific configurations.
     *
     * Throws exceptions if initialization fails.
     */
    void setUp();

    /**
     * Shuts down the HTTP server that was started to expose Prometheus metrics.
     *
     * This method stops the HTTP server if it is running, ensuring that no
     * further metrics will be exposed for scraping.
     */
    void shutdown();
}
