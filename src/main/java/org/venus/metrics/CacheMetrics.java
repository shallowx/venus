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
     * A thread-safe map that maintains a count of hits for specific keys.
     * The keys are strings and the values are Counter instances.
     * This variable is used to keep track of the number of hits for each key.
     */
    private static final ConcurrentHashMap<String, Counter> hitCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that keeps track of the count of missed accesses.
     * The keys are of type String representing the identifiers of the elements.
     * The values are Counter objects that maintain the specific miss count for each identifier.
     */
    private static final ConcurrentHashMap<String, Counter> missCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that stores the count of successful load operations per key.
     * Each key is associated with a {@link Counter} object that tracks the number of successful loads.
     * This map ensures concurrent access without compromising performance.
     */
    private static final ConcurrentHashMap<String, Counter> loadSuccessCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that keeps track of load failure counts using a Counter object.
     * Each entry in the map is identified by a unique String key.
     */
    private static final ConcurrentHashMap<String, Counter> loadFailureCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that holds the total load time for various operations, using counters to track the time.
     * The map keys are strings that represent the names or identifiers of the operations.
     * The values are instances of the Counter class, which encapsulate the actual load time measurement.
     */
    private static final ConcurrentHashMap<String, Counter> totalLoadTime = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that holds counters for eviction events.
     * The keys are strings representing specific entities or events,
     * and the values are Counter objects that maintain the count of evictions.
     * This structure ensures that eviction counts are accurately tracked and can be accessed concurrently without synchronization issues.
     */
    private static final ConcurrentHashMap<String, Counter> evictionCount = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that holds eviction weights for elements identified by a string key.
     * The keys represent the unique identifiers for the items, and the values are
     * Counter objects that track eviction weight metrics.
     */
    private static final ConcurrentHashMap<String, Counter> evictionWeight = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that maintains a count of requests based on their keys.
     * The map uses a {@link ConcurrentHashMap} to handle concurrent access and updates.
     * Each request type is represented by a {@code String} key, and its count is
     * maintained by a {@code Counter} object.
     */
    private static final ConcurrentHashMap<String, Counter> requestCounts = new ConcurrentHashMap<>();
    /**
     * A thread-safe map storing miss rate statistics for various keys.
     * Each entry in the map is identified by a unique string key and
     * is associated with a {@code DistributionSummary} object that
     * tracks statistical data regarding cache misses or similar events.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> missRate = new ConcurrentHashMap<>();
    /**
     * A thread-safe map storing distribution summaries identified by a string key.
     * <p>
     * The key represents the identifier for the distribution summary, and the
     * associated value is an instance of {@link DistributionSummary},
     * which is used to store and compute the statistical distribution of a set of values.
     * This map is designed for concurrent access.
     */
    private static final ConcurrentHashMap<String, DistributionSummary> hitRate = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that stores `DistributionSummary` objects, keyed by a `String`,
     * representing the failure rates of load operations.
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
     * Updates the hit rate metric for the cache.
     *
     * This method retrieves the current cache statistics, specifically
     * the hit rate, and then records this metric using a
     * DistributionSummary. If the DistributionSummary for the hit rate
     * does not exist, it is created and registered with the appropriate tags.
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
     * Updates the miss rate statistic for a cache.
     * <p>
     * This method retrieves the current cache statistics and records the miss rate
     * using a distribution summary. If the distribution summary for the miss rate
     * doesn't exist, it initializes and registers a new summary with appropriate tags.
     * <p>
     * The tags used for the distribution summary include:
     * - application: venus
     * - type: cache_miss_rate
     * - version: 1.0.0
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
     * Records the cache load failure rate into a distribution summary for monitoring purposes.
     *
     * The method retrieves statistics from the cache and checks if a distribution
     * summary for the load failure rate already exists. If it doesn't exist, it creates
     * a new distribution summary with specific tags and registers it. Finally, the method
     * records the load failure rate from the cache statistics into the distribution summary.
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
     * Updates the request count metric for cache requests.
     *
     * This method retrieves the current cache statistics and updates
     * the request count metric in the registry. It first checks if the
     * request count counter is already present in the requestCounts map.
     * If it is not present, it creates and registers a new counter.
     * Finally, it increments the counter by the current request count
     * from the cache statistics.
     */
    public void requestCount() {
        CacheStats stats = cache.stats();
        Counter counter = requestCounts.get("requestCounts");
        if (counter == null) {
            counter = requestCounts.computeIfAbsent("requestCounts",
                    s -> Counter.builder("requestCounts")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_request_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.requestCount());
    }

    /**
     * Updates the hit count metric for a cache.
     *
     * This method retrieves the current cache statistics and increments the hit count metric
     * accordingly. If the hit count metric does not exist, it is created and registered with a set
     * of predefined tags before being incremented.
     */
    public void hitCount() {
        CacheStats stats = cache.stats();
        Counter counter = hitCount.get("hitCount");
        if (counter == null) {
            counter = hitCount.computeIfAbsent("hitCount",
                    s -> Counter.builder("hitCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_hit_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.hitCount());
    }

    /**
     * Updates the miss count for the cache by retrieving the current cache statistics and
     * incrementing the counter associated with the "missCount" metric.
     *
     * If the counter for "missCount" does not already exist, it will be created and
     * registered with the specified tags: "application" set to "venus", "type" set to "cache_miss_count",
     * and "version" set to "1.0.0". The counter is then incremented by the current miss count
     * from the cache statistics.
     *
     * This method assumes the existence of a cache object with a stats() method that provides
     * the current statistics of the cache, including the miss count.
     *
     * It also assumes the existence of a missCount map to store the counters by metric names
     * and a registry object to register new counters.
     */
    public void missCount() {
        CacheStats stats = cache.stats();
        Counter counter = missCount.get("missCount");
        if (counter == null) {
            counter = missCount.computeIfAbsent("missCount",
                    s -> Counter.builder("missCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_miss_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.missCount());
    }

    /**
     * Updates the counter for the number of successful cache loads.
     * <p>
     * This method retrieves the cache statistics and updates the load success count metric.
     * If the metric counter does not exist yet, it will be created and registered.
     * The counter is incremented by the number of successful load operations reported by the cache.
     */
    public void loadSuccessCount() {
        CacheStats stats = cache.stats();
        Counter counter = loadSuccessCount.get("loadSuccessCount");
        if (counter == null) {
            counter = loadSuccessCount.computeIfAbsent("loadSuccessCount",
                    s -> Counter.builder("loadSuccessCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_load_success_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.loadSuccessCount());
    }

    /**
     * Loads the failure count from the cache statistics and updates the corresponding
     * counter in the loadFailureCount map. If the counter does not exist, it initializes
     * and registers a new counter with the specified tags.
     */
    public void loadFailureCount() {
        CacheStats stats = cache.stats();
        Counter counter = loadFailureCount.get("loadFailureCount");
        if (counter == null) {
            counter = loadFailureCount.computeIfAbsent("loadFailureCount",
                    s -> Counter.builder("loadFailureCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_load_failure_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.loadFailureCount());
    }

    /**
     * Calculates the total load time of a cache and increments the corresponding
     * monitoring counter with this value.
     *
     * This method retrieves the cache statistics and updates a counter that tracks
     * the total load time for cache operations. If the counter does not already
     * exist, it is created with appropriate tags and registered in the monitoring
     * registry.
     */
    public void totalLoadTime() {
        CacheStats stats = cache.stats();
        Counter counter = totalLoadTime.get("totalLoadTime");
        if (counter == null) {
            counter = totalLoadTime.computeIfAbsent("totalLoadTime",
                    s -> Counter.builder("totalLoadTime")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_total_load_time")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.totalLoadTime());
    }

    /**
     * Increments the eviction count metric based on the cache statistics.
     * This method retrieves the current eviction count from the cache statistics
     * and updates a Counter metric named "evictionCount". If the counter does not
     * already exist in the evictionCount map, it will create and register a new
     * Counter with relevant tags, and then increment it according to the new
     * eviction count retrieved from the cache stats.
     */
    public void evictionCount() {
        CacheStats stats = cache.stats();
        Counter counter = evictionCount.get("evictionCount");
        if (counter == null) {
            counter = evictionCount.computeIfAbsent("evictionCount",
                    s -> Counter.builder("evictionCount")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_eviction_count")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.evictionCount());
    }


    /**
     * Updates the eviction weight metric for the cache.
     * This method retrieves the current cache statistics to determine the eviction weight.
     * It then increments a counter with the eviction weight value. If the counter does not
     * exist, it is created and registered before the increment operation.
     */
    public void evictionWeight() {
        CacheStats stats = cache.stats();
        Counter counter = evictionWeight.get("evictionWeight");
        if (counter == null) {
            counter = evictionWeight.computeIfAbsent("evictionWeight",
                    s -> Counter.builder("evictionWeight")
                            .tags(Tags.of("application", "venus")
                                    .and("type", "cache_eviction_weight")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment(stats.evictionWeight());
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
