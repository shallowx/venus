package org.venus.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;

/**
 * JmxMetricsRegistry is a concrete implementation of the MetricsRegistrySetUp
 * interface that sets up and manages a JMX MeterRegistry. This class is used
 * to add the JmxMeterRegistry to the global Metrics registry upon initialization.
 *
 * The setUp method initializes the JMX MeterRegistry using the default JMX
 * configuration and system clock, then adds this registry to the global Metrics
 * registry.
 *
 * The shutdown method is currently a no-op, meaning it does nothing when invoked.
 */
public class JmxMetricsRegistry implements MetricsRegistrySetUp {
    /**
     * Initializes and adds the JmxMeterRegistry to the global Metrics registry.
     *
     * This method sets up the JMX MeterRegistry using the default JmxConfig and system clock.
     * It then adds this registry to the global Metrics registry, enabling the application to
     * collect and manage JMX metrics.
     */
    @Override
    public void setUp() {
        JmxMeterRegistry jmxMeterRegistry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);
        Metrics.addRegistry(jmxMeterRegistry);
    }

    /**
     * Shuts down the JMX MeterRegistry. This method is currently a no-op.
     *
     * This method is part of the MetricsRegistrySetUp interface implementation
     * in the JmxMetricsRegistry class. Although it is intended to handle any necessary
     * shutdown procedures for the JMX MeterRegistry, it currently does not perform
     * any actions when invoked.
     *
     * This method can be invoked as part of the lifecycle management of the JmxMetricsRegistry,
     * typically used with frameworks that support init and destroy methods for beans,
     * such as Spring.
     */
    @Override
    public void shutdown() {
        // do nothing
    }
}
