package org.venus.openapi;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.venus.cache.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.venus.cache.MultiLevelCacheConstants.VENUS_REDIRECT_CACHE_NAME;
import static org.venus.cache.MultiLevelCacheConstants.VENUS_CACHE_CALLBACK_NAME;

/**
 * OpenapiService is responsible for managing OpenAPI specifications,
 * including interacting with the database and caching layers,
 * initializing OpenAPI configuration, and handling cache consistency alarms.
 */
@Service
@Slf4j
public class OpenapiService implements IOpenapiService, Callback {

    /**
     * A private final instance of the OpenapiRepository class.
     *
     * This variable is used to interact with the repository layer,
     * providing access to methods for handling OpenAPI specification data.
     * It is marked as final to ensure that the reference cannot be reassigned
     * after initialization.
     */
    private final OpenapiRepository openapiRepository;
    /**
     * The `manager` variable is an instance of the `VenusMultiLevelCacheManager` class.
     * It is responsible for managing a multi-level cache system within the application.
     * This variable is declared as `private` and `final`,
     * indicating that it can only be accessed within the enclosing class
     * and that its reference cannot be changed once initialized.
     */
    private final MultiLevelCacheManager manager;
    /**
     * Represents an instance of the OpenapiCacheConsistentAlarm.
     * This variable is used to manage and initiate alarms related to cache consistency in the OpenAPI.
     * It is declared as final, meaning its reference cannot be changed once assigned.
     * The alarm instance is intended to help maintain the integrity and accuracy of cached data.
     */
    private final OpenapiCacheConsistentAlarm alarm;
    /**
     * The `properties` variable holds configuration properties for initializing
     * the OpenAPI documentation. It is an instance of `OpenapiInitializerProperties`
     * which contains all necessary settings required for setting up the OpenAPI
     * specification.
     */
    private final OpenapiInitializerProperties properties;
    /**
     * RedisTemplate bean for interacting with Redis datastore.
     * Uses String as the key type and CacheWrapper as the value type.
     * Provides operations for performing various Redis commands and transactions.
     * Ensures thread safety and efficiency in accessing Redis data.
     * Can be configured with custom serializers and connection factories.
     */
    private final RedisTemplate<String, CacheWrapper> redisTemplate;
    /**
     * A constant string representing the key-value pair initializer for Venus-related configurations.
     * This is used to specify the initialization parameters that are particular to the Venus component
     * within the system.
     */
    private static final String VENUS_INITIALIZER_KV = "venus-initializer-kv";
    /**
     * A statically initialized instance of {@code ScheduledThreadPoolExecutor} used for scheduling tasks
     * to run after a given delay or to execute periodically. It is configured with a single thread and
     * uses a virtual thread factory to generate threads named "check-primary-cache".
     */
    private static final ScheduledThreadPoolExecutor scheduledPool =(ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1, Thread.ofVirtual().name("check-primary-cache").factory());

    /**
     * Constructs an OpenapiService with specified dependencies.
     *
     * @param openapiRepository the repository to manage OpenAPI entities
     * @param manager the multi-level cache manager
     * @param properties the properties for OpenAPI initialization
     * @param provider the object provider for cache consistent alarm
     * @param redisTemplate the Redis template for cache operations
     */
    @Autowired
    public OpenapiService(OpenapiRepository openapiRepository, MultiLevelCacheManager manager, OpenapiInitializerProperties properties,
                          ObjectProvider<OpenapiCacheConsistentAlarm> provider, RedisTemplate<String, CacheWrapper> redisTemplate) {
        this.openapiRepository = openapiRepository;
        this.manager = manager;
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.alarm = provider.getIfAvailable();
        this.checkMultiLevelCacheIsConsistent();
    }

    /**
     * Initializes the application state by loading necessary data into Redis cache from the database
     * and setting up appropriate keys for the caching mechanism.
     *
     * The method performs the following steps:
     *
     * 1. Checks whether the properties have been initialized.
     * 2. Prevents multiple loads of initialization data by setting a unique key in Redis.
     * 3. Loads entities from the database if the key is successfully set.
     * 4. Populates the cache with either:
     *    a. A shuffled subset of active, valid entities if no hot redirect keys are provided.
     *    b. A filtered list of entities matching the hot redirect keys.
     *
     * Logs warnings if initialization conditions are not met or if the data set from the database is empty.
     * Logs errors if there's an exception during the cache population process.
     *
     * This method is marked with {@code @PostConstruct}, ensuring it's executed once the bean's properties have been initialized.
     */
    @PostConstruct
    public void init() {
        if (!properties.isInitialized()) {
            if (log.isWarnEnabled()) {
                log.warn("Venus redis initialize not need");
            }
            return;
        }

        // Prevent restarts or expansions from causing the service to load initialization data multiple times
        // And under normal circumstances, there is no need to set the expiration time for the key or delete the key
        // If the pod starts abnormally, you can choose to manually delete and change the key, or accept the method of initializing the cache when accessing the key to initialize the data
        Boolean isSuccess = redisTemplate.opsForValue().setIfAbsent(VENUS_INITIALIZER_KV, new CacheWrapper(VENUS_INITIALIZER_KV, VENUS_INITIALIZER_KV));
        if (Boolean.FALSE.equals(isSuccess)) {
            if (log.isInfoEnabled()) {
                log.info("venus redirect initializer was successfully");
            }
            return;
        }

        List<ValueWrapper> entities = this.lists();
        if (entities == null || entities.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("Venus redis initialize from db is empty");
            }
            return;
        }

        Cache cache = manager.getCache(VENUS_REDIRECT_CACHE_NAME);
        List<String> hotRedirectKeys = properties.getHotRedirectKeys();
        if (hotRedirectKeys == null || hotRedirectKeys.isEmpty()) {
            Collections.shuffle(entities);
            List<ValueWrapper> activeEntities = entities.stream().filter(e -> {
                short isActive = e.getIsActive();
                LocalDateTime expiresAt = e.getExpiresAt();
                return OpenapiRedirectStatusEnum.ACTIVE == OpenapiRedirectStatusEnum.of(isActive) && expiresAt.isAfter(LocalDateTime.now());
            }).toList().subList(0, Math.min(properties.getMaxRandomRedirectKeys(), entities.size()));
            activeEntities.forEach(valueWrapper -> cache.put(valueWrapper.getCode(), valueWrapper));
        } else {
            for (ValueWrapper valueWrapper : entities) {
                String key = valueWrapper.getCode();
                short isActive = valueWrapper.getIsActive();
                LocalDateTime expiresAt = valueWrapper.getExpiresAt();

                if (OpenapiRedirectStatusEnum.UN_ACTIVE == OpenapiRedirectStatusEnum.of(isActive) || expiresAt.isBefore(LocalDateTime.now())) {
                    continue;
                }
                try {
                    if (hotRedirectKeys.contains(key)) {
                        cache.put(valueWrapper.getCode(), valueWrapper);
                    }
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Venus redis key[{}] initialize was failure with entity[{}]", valueWrapper.getCode(), valueWrapper, e);
                    }
                }
            }
        }
    }

    /**
     * Retrieves an OpenapiEntity from the repository based on the provided encode key.
     *
     * @param encode a String representing the key used to fetch the desired OpenapiEntity.
     * @return the OpenapiEntity associated with the provided encode key.
     */
    @MultiLevelCache(cacheName = VENUS_REDIRECT_CACHE_NAME, key = "#encode", type = MultiLevelCacheType.ALL)
    @Override
    public ValueWrapper get(String encode) {
        OpenapiEntity entity = openapiRepository.get(encode);
        if (entity == null) {
            return ValueWrapper.builder().build();
        }
        return ValueWrapper.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .originalUrl(entity.getOriginalUrl())
                .redirect(entity.getRedirect())
                .expiresAt(entity.getExpiresAt())
                .isActive(entity.getIsActive())
                .build();
    }


    /**
     * Retrieves a list of OpenapiEntity objects that are active and have not expired.
     * The method filters the entities based on their 'isActive' status being non-zero
     * and their 'expiresAt' timestamp being after the current date and time.
     *
     * @return a list of active and non-expired OpenapiEntity objects
     */
    @Override
    public List<ValueWrapper> lists() {
        return openapiRepository.lists().stream()
                .filter(f -> f.getIsActive() != 0 && f.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(v -> ValueWrapper.builder()
                        .id(v.getId())
                        .code(v.getCode())
                        .originalUrl(v.getOriginalUrl())
                        .redirect(v.getRedirect())
                        .expiresAt(v.getExpiresAt())
                        .isActive(v.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Redirects to an {@link OpenapiEntity} based on the provided encode string.
     * This method checks the cache for the entity and verifies its active status
     * and expiry before returning it.
     *
     * @param encode The encoded string used to look up the OpenapiEntity.
     * @return The OpenapiEntity if found, active, and not expired; otherwise, returns null.
     */
    @MultiLevelCache(cacheName = VENUS_REDIRECT_CACHE_NAME, key = "#encode", type = MultiLevelCacheType.ALL)
    @Override
    public ValueWrapper redirect(String encode) {
        ValueWrapper entity = this.get(encode);
        if (entity == null
                || OpenapiRedirectStatusEnum.UN_ACTIVE == OpenapiRedirectStatusEnum.of(entity.getIsActive())
                || entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }
        return entity;
    }

    /**
     * Handles the callback for updating the Venus cache with the given parameters.
     *
     * @param key The key associated with the cache entry.
     * @param o The object to be cached.
     * @param type The type of the cache entry.
     */
    @Override
    public void callback(String key, Object o, String type) {
        CallbackRequestEntity entity = CallbackRequestEntity.builder().key(key).o(o).type(type).build();
        try {
            CacheSelector selector = (MultiLevelValueAdaptingCache) manager.getCache(VENUS_REDIRECT_CACHE_NAME);
            selector.secondCache().convertAndSend(VENUS_CACHE_CALLBACK_NAME, entity);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Venus cache update callback failure", e);
            }
            alarm.alarm(key, o, type);
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    static class CallbackRequestEntity implements Serializable {
        /**
         * Serial version UID for ensuring compatibility during serialization and deserialization.
         */
        @Serial
        private static final long serialVersionUID = 2717227104050710204L;
        /**
         * Represents a unique identifier for the callback request.
         */
        private String key;
        /**
         * An arbitrary object associated with the callback request.
         * This may hold any data type or additional information needed for handling the request.
         */
        private Object o;
        /**
         * The type of callback request, indicating whether it is an "update" or "evict" operation.
         */
        private String type; // update or evict
    }

    /**
     * Ensures that the multi-level cache is consistent by periodically scheduling a consistency task.
     *
     * This method retrieves the primary and secondary caches from a {@link MultiLevelValueAdaptingCache} instance.
     * It then creates a {@link ConsistentTask} which checks the consistency between the primary cache and the secondary Redis cache.
     * The task is scheduled at fixed intervals with an initial delay using a scheduled executor service.
     *
     * In case of an exception during scheduling, it logs an error message and clears the scheduled task queue before
     * attempting to reschedule the consistency task.
     */
    private void checkMultiLevelCacheIsConsistent() {
        CacheSelector selector = (MultiLevelValueAdaptingCache)manager.getCache(VENUS_REDIRECT_CACHE_NAME);
        com.github.benmanes.caffeine.cache.Cache<String, Object> primaryCache = selector.primaryCache();
        RedisTemplate<String, CacheWrapper> secondCache = selector.secondCache();
        OpenapiService.ConsistentTask consistentTask = new ConsistentTask(primaryCache, secondCache, alarm);
        try {
            scheduledPool.scheduleAtFixedRate(consistentTask, properties.getInitialDelay().toMillis(), properties.getCheckPrimaryCachePeriod().toMillis(), TimeUnit.MILLISECONDS);
        }catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error("Venus check cache consistent failure", e);
            }
            if (!scheduledPool.isShutdown()) {
                scheduledPool.scheduleAtFixedRate(consistentTask, properties.getInitialDelay().toMillis(), properties.getCheckPrimaryCachePeriod().toMillis(), TimeUnit.MILLISECONDS);
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("The multi-level cache consistency check task will be canceled when the thread pool stops running");
                }
            }
        }
    }

    static class ConsistentTask implements Runnable {
        /**
         * A primary cache for storing key-value pairs using the Caffeine caching library.
         * This cache is used as the main cache in the ConsistentTask class to store and retrieve
         * objects efficiently.
         *
         * The primaryCache variable is thread-safe and designed to handle high-concurrency scenarios,
         * providing optimal performance for common caching needs. It is marked as final to ensure
         * that its reference does not change once initialized.
         *
         * The cache is expected to store String keys with their corresponding Object values.
         *
         * Note: com.github.benmanes.caffeine.cache.Cache library is used for the cache implementation.
         */
        private final com.github.benmanes.caffeine.cache.Cache<String, Object> primaryCache;

        /**
         * The secondary cache that uses Redis as its storage mechanism.
         * This cache is used to ensure data consistency by providing a backup
         * to the primary in-memory cache and serves as the source of truth
         * during consistency checks.
         */
        private final RedisTemplate<String, CacheWrapper> secondCache;
        /**
         * The `alarm` field is an instance of the `OpenapiCacheConsistentAlarm` interface
         * used to handle cache inconsistency alerts in the ConsistentTask class.
         * It is initialized through the constructor and utilized to signal inconsistencies
         * in the cache layers by invoking its `alarm` method when discrepancies are detected
         * between the primary and secondary cache.
         */
        private final OpenapiCacheConsistentAlarm alarm;

        /**
         * Initializes a new instance of ConsistentTask with the specified primary cache, second cache, and alarm.
         *
         * @param primaryCache the primary in-memory cache used for storing data
         * @param secondCache the secondary cache using Redis for data storage
         * @param alarm the alarm mechanism used to signal cache inconsistencies
         */
        public ConsistentTask(com.github.benmanes.caffeine.cache.Cache<String, Object> primaryCache, RedisTemplate<String, CacheWrapper> secondCache, OpenapiCacheConsistentAlarm alarm) {
            this.primaryCache = primaryCache;
            this.secondCache = secondCache;
            this.alarm = alarm;
        }

        /**
         * Checks the consistency between a primary cache and a secondary cache,
         * updating the primary cache to reflect the state of the secondary cache.
         *
         * This method performs the following actions:
         * - Retrieves all entries from the primary cache.
         * - Logs a warning and returns if the primary cache is empty.
         * - Retrieves corresponding entries from the secondary cache.
         * - Logs a debug message and returns if the secondary cache is empty.
         * - Iterates over the key-value pairs in the primary cache and verifies if each entry is
         *   present in the secondary cache:
         *   - Logs a warning, invalidates the primary cache entry, and triggers an alarm if the
         *     key from the primary cache is missing in the secondary cache.
         *   - If the key is present in both caches but the values differ, updates the primary
         *     cache with the value from the secondary cache.
         *   - Logs a debug message if the key and value match between the caches.
         * - Catches and logs any exceptions that occur during the execution and triggers an alarm
         *   indicating a consistent check failure.
         */
        @Override
        public void run() {
            try {
                ConcurrentMap<String, Object> primaryCacheMap = primaryCache.asMap();
                if (primaryCacheMap.isEmpty()) {
                    if (log.isWarnEnabled()) {
                        log.warn("Primary cache is empty");
                    }
                    return;
                }

                Set<String> keySet = primaryCacheMap.keySet();
                List<CacheWrapper> secondCacheWrappers = secondCache.opsForValue().multiGet(keySet);
                if (secondCacheWrappers == null || secondCacheWrappers.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        log.debug("SecondCache cache is empty");
                    }
                    return;
                }

                // if the primary-cache value is not the same as the second-cache,and then it's will update the primary-cache value from the second-cache value
                List<String> secondCacheKeys = secondCacheWrappers.stream().map(CacheWrapper::getKey).toList();
                primaryCacheMap.forEach((k, v) -> {
                    if (v instanceof CacheWrapper) {
                        String key = ((CacheWrapper) v).getKey();
                        Object value = ((CacheWrapper) v).getValue();
                        if (!secondCacheKeys.contains(key)) {
                            if (log.isWarnEnabled()) {
                                log.warn("Venus primary cache key[{}] is not exists in secondCache, will be remote it", key);
                            }
                            primaryCache.invalidate(key);
                            alarm.alarm(key, value, "evict");
                        } else {
                            for (CacheWrapper cacheWrapper : secondCacheWrappers) {
                                if (key.equals(cacheWrapper.getKey()) && value.equals(cacheWrapper.getValue())) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Primary cache of key[{}] the same as the second cache", key);
                                    }
                                    continue;
                                }
                                // put the new value of the cache key
                                primaryCache.put(key, cacheWrapper);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Venus check cache consistent failure", e);
                }
                alarm.alarm("check-cache-consistent", e, "check-cache-consistent");
            }
        }
    }

    /**
     * The destroy method is annotated with @PreDestroy and is intended to be called
     * when the application is shutting down. It ensures that the scheduledPool is properly
     * shut down if it has not already been done.
     *
     * If the scheduledPool is not already in a shutdown state, this method invokes the
     * shutdown process to release resources and properly terminate any scheduled tasks.
     */
    @PreDestroy
    public void destroy() {
        if (!scheduledPool.isShutdown()) {
            scheduledPool.shutdownNow();
        }
    }
}
