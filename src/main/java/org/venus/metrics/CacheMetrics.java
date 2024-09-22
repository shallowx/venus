package org.venus.metrics;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The CacheMetrics class is responsible for collecting and reporting metrics
 * for a given cache instance. This includes hit counts, miss counts, load success
 * and failure counts, total load time, eviction counts, and eviction weights.
 *
 * The metrics are periodically collected and reported using a scheduled task.
 *
 * The metrics are registered with a global metrics registry.
 */
@Slf4j
public class CacheMetrics {

    /**
     * A thread-safe map that stores the distribution summary for hit counts.
     *
     * This map uses string keys to represent different categories or identifiers for
     * which the hit counts are maintained. The values are instances of
     * DistributionSummary, which summarize statistical data of the hits.
     *
     * The use of ConcurrentHashMap ensures that the operations on the map
     * are thread-safe and can be safely used in a concurrent context without
     * requiring explicit synchronization.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> hitCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that holds distribution summaries for recording miss counts.
     * Each key is a string representing an entity whose miss counts are being tracked.
     * The value is a DistributionSummary object that maintains the statistics for the miss counts.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> missCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that holds the count of successful load operations,
     * keyed by a load identifier of type String. Each entry in the map is a
     * DistributionSummary object that keeps track of distribution statistics
     * for the successful load operations associated with the corresponding key.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> loadSuccessCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that maintains a summary of load failures for different keys.
     * Each key is associated with a DistributionSummary, which provides statistical
     * information about the load failure occurrences.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> loadFailureCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe, concurrent hashmap that holds DistributionSummary objects, keyed by string.
     * Each entry represents the total load time distribution for a specific category or identifier.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> totalLoadTime = new ConcurrentHashMap<>();
    /**
     * A concurrent hash map to track eviction counts in a thread-safe manner.
     * The keys are strings representing specific identifiers, and the
     * values are DistributionSummary objects that record statistical
     * distribution information about eviction events.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> evictionCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe concurrent hash map that stores instances of DistributionSummary
     * keyed by a String identifier. This map is used to track the eviction weights
     * of various elements, ensuring that operations on this structure are safe in
     * a multi-threaded environment.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> evictionWeight = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that maintains distribution summaries for request counts.
     *
     * Each key is a string representing a unique identifier for a type of request.
     * Each value is a DistributionSummary object that collects and summarizes
     * statistical distributions of these request counts.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> requestCounts = new ConcurrentHashMap<>();
    /**
     * missRate is a thread-safe map that stores DistributionSummary objects
     * associated with String keys. It is used to record and summarize the
     * distribution of events or measurements, typically for tracking cache
     * miss rates or other metrics.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> missRate = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that stores instances of {@code DistributionSummary}
     * indexed by a {@code String} key. This variable is used to keep track of
     * hit rate metrics for various entities identified by the keys.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> hitRate = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that holds {@link DistributionSummary} instances indexed by a string key.
     * This map is used to track the failure rates of load operations and allows concurrent access
     * from multiple threads to ensure performance and consistency in a multi-threaded environment.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> loadFailureRate = new ConcurrentHashMap<>();
    /**
     * A final cache instance to hold key-value pairs where the key is a {@code String} and the value is an {@code Object}.
     * This cache is used to store and retrieve objects efficiently to enhance performance and reduce repetitive computations or data retrieval.
     */
    private final Cache<String, Object> cache;
    /**
     * A singleton instance of MeterRegistry that serves as the global registry for managing all metrics.
     * It is initialized with the global registry provided by the Metrics library.
     * This registry should be used to register, manage, and access all metrics across the application.
     *
     * Example usage:
     * - Registering a counter: Counter.builder("example.counter").register(registry);
     * - Retrieving a counter: registry.get("example.counter").counter();
     *
     * Benefits:
     * - Centralized management of all application metrics.
     * - Consistent access to metrics across different components of the application.
     *
     * Note:
     * - Ensure that this registry is properly configured and initialized at the start of the application.
     * - As a global registry, it is designed to be thread-safe and shared across multiple threads.
     */
    private static final MeterRegistry registry = Metrics.globalRegistry;
    /**
     * A statically initialized {@code ScheduledThreadPoolExecutor} instance.
     *
     * This pool is configured with a single virtual thread using a custom thread
     * factory named "primary-cache-metrics". It schedules periodic or delayed
     * tasks for execution.
     *
     * The pool is initialized using {@code Executors.newScheduledThreadPool} and
     * cast to {@code ScheduledThreadPoolExecutor}.
     */
    private static final ScheduledThreadPoolExecutor scheduledPool =(ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1, Thread.ofVirtual().name("primary-cache-metrics").factory());
    /**
     * Represents the duration of a specific period.
     * This variable is immutable and is initialized during instantiation.
     */
    private final Duration period;
    /**
     * A boolean variable that indicates if a feature or functionality is enabled.
     * When set to true, the associated feature will be active.
     * When set to false, the feature will be inactive.
     */
    private final boolean enabled;

    /**
     * Constructs a CacheMetrics object that tracks metrics for a given cache.
     *
     * @param cache the cache instance to track metrics for
     * @param period the duration at which metrics should be collected
     * @param enabled flag to indicate if metric collection is enabled
     */
    public CacheMetrics(Cache<String, Object> cache, Duration period, boolean enabled) {
        this.cache = cache;
        this.enabled = enabled;
        this.period = period;
    }

    /**
     * Initializes the metrics collection for the cache system.
     *
     * This method schedules a recurring task to collect metrics at fixed intervals,
     * as specified by the period. If the 'enabled' flag is false, the method exits
     * without scheduling any tasks. In case of an exception while scheduling the
     * task, it logs an error and retries the scheduling.
     */
    public void init() {
        if (!enabled) {
            return;
        }
        MetricsTask metricsTask = new MetricsTask(this);
        try {
            scheduledPool.scheduleAtFixedRate(metricsTask, 0, period.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Cache metrics init failed", e);
            }
            scheduledPool.scheduleAtFixedRate(metricsTask, 0, period.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    static class MetricsTask implements Runnable {
        /**
         * An instance of CacheMetrics that holds various cache performance metrics.
         * This includes metrics such as hit count, miss count, load success count,
         * load failure count, total load time, eviction count, and eviction weight.
         * Used within the MetricsTask class to periodically record and log the cache performance data.
         */
        private final CacheMetrics cacheMetrics;
        /**
         * Constructs a new MetricsTask to collect and log cache metrics.
         *
         * @param cacheMetrics the cache metrics to be collected and logged
         */
        public MetricsTask(CacheMetrics cacheMetrics) {
            this.cacheMetrics = cacheMetrics;
        }

        /**
         * Executes the cache metric collection tasks.
         *
         * This method retrieves various cache performance metrics such as hit count, miss count,
         * load success count, load failure count, total load time, eviction count, eviction weight,
         * load failure rate, hit rate, miss rate, and request count. These metrics are then updated
         * and recorded. In case of an exception during this process, an error message is logged.
         *
         * If the log is configured to capture error messages, it logs the error message "Cache metrics
         * task failed" along with the exception details.
         */
        @Override
        public void run() {
           try {
               this.cacheMetrics.hitCount();
               this.cacheMetrics.missCount();
               this.cacheMetrics.loadSuccessCount();
               this.cacheMetrics.loadFailureCount();
               this.cacheMetrics.totalLoadTime();
               this.cacheMetrics.evictionCount();
               this.cacheMetrics.evictionWeight();
               this.cacheMetrics.loadFailureRate();
               this.cacheMetrics.hitRate();
               this.cacheMetrics.missRate();
               this.cacheMetrics.requestCount();
           } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Cache metrics task failed", e);
                }
           }
        }
    }

    /**
     * The hitRate method records the hit rate statistics of the cache.
     * It retrieves the CacheStats and records the hit rate in a DistributionSummary.
     * If a DistributionSummary for "hitRate" does not already exist, it is created
     * and registered with the specified tags.
     * This method is intended to be used for monitoring cache performance metrics.
     */
    public void hitRate() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = hitRate.get("hitRate");
        if (summary == null) {
            summary = hitRate.computeIfAbsent("hitRate",
                    s -> DistributionSummary.builder("hitRate")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_hit_rate")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.hitRate());
    }

    /**
     * Records the cache miss rate using a distribution summary.
     *
     * This method retrieves the cache statistics and updates the miss rate
     * distribution summary with the current cache miss rate metrics.
     * If the summary does not exist, it initializes and registers a new one
     * with predefined tags representing the application, type, and version.
     *
     * @throws NullPointerException if cache or registry is null
     */
    public void missRate() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = missRate.get("missRate");
        if (summary == null) {
            summary = missRate.computeIfAbsent("missRate",
                    s -> DistributionSummary.builder("missRate")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_miss_rate")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.missRate());
    }

    /**
     * Loads and records the cache load failure rate using a {@link DistributionSummary}.
     *
     * This method fetches the current cache statistics and retrieves or creates
     * a {@link DistributionSummary} to track the load failure rate of the cache.
     * The failure rate is recorded in the summary.
     *
     * The summary is tagged with information such as the application name, type,
     * and version.
     *
     * Note: The method assumes that the cache and the loadFailureRate map are properly initialized
     * and accessible within the class context.
     */
    public void loadFailureRate() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = loadFailureRate.get("loadFailureRate");
        if (summary == null) {
            summary = loadFailureRate.computeIfAbsent("loadFailureRate",
                    s -> DistributionSummary.builder("loadFailureRate")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_load_failure_rate")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.loadFailureRate());
    }

    /**
     * Records the cache request count in a distribution summary metric.
     * This method retrieves the cache statistics and records the request count
     * in a pre-defined distribution summary named "requestCounts". If the
     * distribution summary does not exist in the map, it initializes and registers one.
     *
     * The distribution summary is tagged with:
     * - application: "venus"
     * - type: "cache_request_count"
     * - version: "1.0.0"
     */
    public void requestCount() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = requestCounts.get("requestCounts");
        if (summary == null) {
            summary = requestCounts.computeIfAbsent("requestCounts",
                    s -> DistributionSummary.builder("requestCounts")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_request_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.requestCount());
    }

    /**
     * Updates the hit count metric for the cache.
     * This method retrieves the cache statistics and records the hit count to a
     * distribution summary named "hitCount". If the summary does not exist, it creates
     * and registers a new one with specific tags.
     */
    public void hitCount() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = hitCount.get("hitCount");
        if (summary == null) {
            summary = hitCount.computeIfAbsent("hitCount",
                    s -> DistributionSummary.builder("hitCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_hit_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.hitCount());
    }

    /**
     * Records the number of cache misses using a DistributionSummary.
     *
     * This method collects the current cache stats and updates a DistributionSummary
     * with the latest miss count. If the DistributionSummary for "missCount" does not exist,
     * it initializes and registers it with specific tags.
     */
    public void missCount() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = missCount.get("missCount");
        if (summary == null) {
            summary = missCount.computeIfAbsent("missCount",
                    s -> DistributionSummary.builder("missCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_miss_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.missCount());
    }

    /**
     * Tracks and records the number of successful cache load operations.
     *
     * This method retrieves the cache statistics and records the count of successful
     * cache loads in a distribution summary. If the summary does not exist, it is
     * created and registered in the specified registry with appropriate tags.
     */
    public void loadSuccessCount() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = loadSuccessCount.get("loadSuccessCount");
        if (summary == null) {
            summary = loadSuccessCount.computeIfAbsent("loadSuccessCount",
                    s -> DistributionSummary.builder("loadSuccessCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_load_success_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.loadSuccessCount());
    }

    /**
     * Updates the load failure count metric for the cache.
     *
     * This method retrieves cache statistics, specifically the load failure count,
     * and records it in a summary distribution after creating or retrieving the
     * appropriate metric summary.
     *
     * The method assumes that a cache object and a registry for metrics are
     * already set up and available for use.
     */
    public void loadFailureCount() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = loadFailureCount.get("loadFailureCount");
        if (summary == null) {
            summary = loadFailureCount.computeIfAbsent("loadFailureCount",
                    s -> DistributionSummary.builder("loadFailureCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_load_failure_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.loadFailureCount());
    }

    /**
     * Records the total load time of the cache into a distribution summary.
     *
     * This method collects the total load time statistics from a cache and
     * records it in a distribution summary. The summary is registered with a
     * monitoring system if it is not already present.
     *
     * The recorded metrics contain tags for application name ("venus"),
     * metric type ("cache_total_load_time"), and version ("1.0.0").
     */
    public void totalLoadTime() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = totalLoadTime.get("totalLoadTime");
        if (summary == null) {
            summary = totalLoadTime.computeIfAbsent("totalLoadTime",
                    s -> DistributionSummary.builder("totalLoadTime")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_total_load_time")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.totalLoadTime());
    }

    /**
     * Records the number of cache evictions.
     *
     * This method retrieves cache statistics, specifically the eviction count,
     * and records this value in a distribution summary. If the distribution summary
     * for "evictionCount" does not exist, it initializes and registers a new one
     * with specific tags, including application name, type, and version.
     */
    public void evictionCount() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = evictionCount.get("evictionCount");
        if (summary == null) {
            summary = evictionCount.computeIfAbsent("evictionCount",
                    s -> DistributionSummary.builder("evictionCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_eviction_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.evictionCount());
    }


    /**
     * Records the eviction weight of the cache into a metrics summary.
     * This method retrieves the cache statistics and updates the eviction weight
     * metric. If the eviction weight summary does not exist, it creates a new summary
     * with appropriate tags and registers it in the metrics registry.
     */
    public void evictionWeight() {
        CacheStats stats = cache.stats();
        DistributionSummary summary = evictionWeight.get("evictionWeight");
        if (summary == null) {
            summary = evictionWeight.computeIfAbsent("evictionWeight",
                    s -> DistributionSummary.builder("evictionWeight")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_eviction_weight")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        summary.record(stats.evictionWeight());
    }

    /**
     * Shuts down the scheduled thread pool gracefully.
     * Initially, it will attempt to stop further tasks from being scheduled and allow existing tasks to finish execution.
     * If the pool does not terminate within the specified timeout, a forceful shutdown is initiated to cancel active tasks.
     * Handles InterruptedException by re-attempting a shutdown and restoring the interrupt status.
     */
    public void shutdown() {
        if (!scheduledPool.isShutdown()) {
            scheduledPool.shutdown();
        }
        try {
            if (!scheduledPool.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduledPool.shutdownNow();
                if (!scheduledPool.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.err.println("Metrics pool did not terminate, and it's will be closed");
                }
            }
        } catch (InterruptedException ie) {
            scheduledPool.shutdownNow();
            Thread.currentThread().interrupt();
            if (log.isErrorEnabled()) {
                log.error("Metrics pool shutdown failure, and it's will be closed", ie);
            }
        }
    }
}
