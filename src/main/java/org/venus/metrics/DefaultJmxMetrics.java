package org.venus.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;

/**
 * DefaultJmxMetrics is responsible for initializing and registering various
 * JVM-related metrics to the global MeterRegistry. These metrics include uptime,
 * file descriptor usage, class loading, memory usage, garbage collection,
 * compilation, JVM information, thread usage, and processor metrics.
 *
 * This class uses the Micrometer library to bind JVM metrics to the global
 * MeterRegistry, which are tagged with application name and version for easier
 * identification and filtering.
 *
 * The following metrics are initialized and bound:
 *
 * - UptimeMetrics: Tracks the application's uptime.
 * - FileDescriptorMetrics: Monitors file descriptor usage.
 * - ClassLoaderMetrics: Provides insights into class loading operations.
 * - JvmMemoryMetrics: Captures JVM memory usage statistics.
 * - JvmGcMetrics: Records garbage collection statistics.
 * - JvmCompilationMetrics: Tracks JVM compilation activity.
 * - JvmInfoMetrics: Provides basic JVM information.
 * - JvmThreadMetrics: Provides statistics on JVM threads.
 * - ProcessorMetrics: Captures processor information and usage.
 */
@SuppressWarnings("all")
public class DefaultJmxMetrics {
    private static final Tags tags = Tags.of("application", "venus").and("version", "1.0.0");

    public DefaultJmxMetrics() {
        MeterRegistry registry = Metrics.globalRegistry;
        new UptimeMetrics(tags).bindTo(registry);
        new FileDescriptorMetrics(tags).bindTo(registry);
        new ClassLoaderMetrics(tags).bindTo(registry);
        new JvmMemoryMetrics(tags).bindTo(registry);
        new JvmGcMetrics(tags).bindTo(registry);
        new JvmCompilationMetrics(tags).bindTo(registry);
        new JvmInfoMetrics().bindTo(registry);
        new JvmThreadMetrics(tags).bindTo(registry);
        new ProcessorMetrics(tags).bindTo(registry);
    }
}
