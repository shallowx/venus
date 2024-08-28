package org.venus.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;

public class JmxMetricsRegistry implements MetricsRegistrySetUp {
    @Override
    public void setUp() {
        JmxMeterRegistry jmxMeterRegistry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);
        Metrics.addRegistry(jmxMeterRegistry);
    }

    @Override
    public void shutdown() {
        // do nothing
    }
}
