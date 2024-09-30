package org.venus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.venus.cache.MultiLevelCacheConstants.DEFAULT_LISTENER_NAME;


/**
 * A multi-level caching system that integrates a primary in-memory cache with a secondary Redis cache.
 * This class is designed to enhance cache retrieval performance by first attempting to get the cached value
 * from the primary (in-memory) cache and, if not found, retrieving it from the secondary (Redis) cache.
 */
@Slf4j
public class MultiLevelValueAdaptingCache extends AbstractValueAdaptingCache implements CacheSelector {
    /**
     * Represents the name of the cache.
     * This variable holds the identifier used for the cache.
     */
    private String cacheName;
    /**
     * Redis template instance used for interacting with the second level cache.
     * This cache stores wrapped cache entries in a Redis datastore.
     * The keys are of type String and the values are instances of CacheWrapper.
     */
    private RedisTemplate<String, CacheWrapper> secondCache;
    /**
     * A primary cache instance used to store and retrieve objects.
     * It uses a key of type String and a value of type Object.
     * The cache serves as the main storage for frequently accessed data to optimize retrieval times.
     */
    private Cache<String, Object> primaryCache;
    /**
     * An instance of VenusMultiLevelCacheProperties that holds the configuration
     * settings for the multi-level caching system.
     */
    private MultiLevelCacheProperties properties;

    /**
     * Constructs a VenusMultiLevelValueAdaptingCache.
     *
     * @param cacheName the name of the cache.
     * @param template the Redis template to be used for the secondary cache.
     * @param primaryCache the primary cache to be used.
     * @param properties properties for configuring the multi-level cache.
     */
    public MultiLevelValueAdaptingCache(String cacheName, RedisTemplate<String, CacheWrapper> template, Cache<String, Object> primaryCache, MultiLevelCacheProperties properties) {
        super(properties.isAllowNull());
        this.cacheName = cacheName;
        this.secondCache = template;
        this.primaryCache = primaryCache;
        this.properties = properties;
    }

    /**
     * Constructor for VenusMultiLevelValueAdaptingCache.
     *
     * @param allowNullValues - Flag to allow or disallow null values in the cache.
     */
    protected MultiLevelValueAdaptingCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    /**
     * Retrieves an object associated with the provided key from the primary cache or, if not found,
     * from the secondary cache. If the object is found in the secondary cache, it is stored in
     * the primary cache for faster future retrievals.
     *
     * @param key the key whose associated object is to be retrieved
     * @return the object associated with the specified key, or null if no such object exists
     */
    @Override
    protected Object lookup(@NonNull Object key) {
        CacheWrapper wrapper = (CacheWrapper) primaryCache.getIfPresent((String) key);
        if (wrapper != null) {
            if (log.isDebugEnabled()) {
                log.debug("Get data[key:{}, value-wrapper:{}] from primary cache", key, wrapper);
            }
            return wrapper;
        }

        String redisKey = buildKey(key);
        wrapper = secondCache.opsForValue().get(redisKey);
        if (wrapper != null) {
            if (log.isDebugEnabled()) {
                log.debug("Get data[key:{}, value-wrapper:{}] from second cache", key, wrapper);
            }
            primaryCache.put((String) key, wrapper);
        }
        return wrapper;
    }

    /**
     * Retrieves the name of the cache.
     *
     * @return the name of the cache as a non-null string.
     */
    @NonNull
    @Override
    public String getName() {
        return this.cacheName;
    }

    /**
     * Retrieves the native cache object associated with this instance.
     *
     * @return the native cache object
     */
    @NonNull
    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * Retrieves the value associated with the given key from the cache, loading it using the provided
     * valueLoader if it is not already present.
     *
     * @param key The key whose associated value is to be returned. It should not be null.
     * @param valueLoader A callable used to load the value if it is not present in the cache. It should not be null.
     * @param <T> The type of the value.
     * @return The value associated with the key, or a newly loaded value if the key was not present in the cache.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        try {
            CacheWrapper wrapper = (CacheWrapper) lookup(key);
            if (wrapper != null) {
                return (T) wrapper.getValue();
            }
            T t = valueLoader.call();
            synchronized (this) {
                wrapper = (CacheWrapper) lookup(key);
                if (wrapper != null) {
                    return (T) wrapper.getValue();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("The key[{}]-value[{}] is not exists in the cache", key, t);
                    }
                    put(key, t);
                }
            }
            return t;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Get data[key:{}] from cache is failure", key, e);
            }
        }
        return null;
    }

    /**
     * Stores the given key-value pair in the cache. If the cache is configured
     * to disallow null values and the provided value is null, then the key-value
     * pair will not be stored, and a warning will be logged.
     *
     * @param key   the key with which the specified value is to be associated; must not be null
     * @param value the value to be associated with the specified key; can be null depending on configuration
     */
    @Override
    public void put(@NonNull Object key, Object value) {
        // Allows storage of NULL values, which in some cases can avoid problems such as cache penetration
        if (!isAllowNullValues() && value == null) {
            if (log.isWarnEnabled()) {
                log.warn("The key[{}] of value 'NULL' will not be cached", key);
            }
            return;
        }
        primaryCache.put((String) key, new CacheWrapper((String) key, value));

        String redisKey = buildKey(key);
        // the second cache is not permits the key-value expire
        secondCache.opsForValue().set(redisKey, new CacheWrapper(redisKey, value));
        try {
            CacheListenerMessage cacheMassage = CacheListenerMessage.builder()
                    .name(this.cacheName)
                    .type(CacheMessageListenerType.UPDATE)
                    .value(value)
                    .key((String) key)
                    .source(ListenerSourceSupport.getSourceAddress())
                    .build();
            secondCache.convertAndSend(DEFAULT_LISTENER_NAME, cacheMassage);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Evicts an entry with the specified key from both primary and secondary caches.
     *
     * @param key the key of the entry to be evicted
     */
    @Override
    public void evict(@NonNull Object key) {
        secondCache.delete(buildKey(key));
        primaryCache.invalidate((String) key);
        try {
            CacheListenerMessage message = CacheListenerMessage.builder()
                    .name(this.cacheName)
                    .type(CacheMessageListenerType.INVALIDATE)
                    .key((String) key)
                    .source(ListenerSourceSupport.getSourceAddress())
                    .build();

            secondCache.convertAndSend(DEFAULT_LISTENER_NAME, message);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Clears the cache by removing all entries associated with the specified cache name.
     *
     * This method will retrieve all keys that match the pattern specified by the
     * cache name concatenated with ":*" and delete them from the secondary cache.
     * Additionally, it will invalidate all entries in the primary cache.
     */
    @Override
    public void clear() {
        Set<String> keys = getCacheKeys(this.cacheName.concat(":*"));
        secondCache.delete(keys);
        primaryCache.invalidateAll();
    }

    /**
     * Retrieves a set of cache keys that match the given pattern.
     *
     * @param pattern the pattern to match cache keys against.
     * @return a set of cache keys that match the specified pattern.
     */
    public Set<String> getCacheKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions()
                .count(properties.getRedisScanCount())
                .type(DataType.STRING)
                .match(pattern)
                .build();
        Cursor.CursorId cursorId;
        do {
            Cursor<String> cursor = secondCache.scan(options);
            cursorId = cursor.getId();
        } while (!"0".equals(cursorId.getCursorId()));
        return keys;
    }

    /**
     * Constructs a cache key by combining the cache name with the provided key.
     *
     * @param key the object to be used as a key in the cache
     * @return a concatenated string representing the full cache key
     */
    private String buildKey(Object key) {
        return this.cacheName + ":" + key;
    }

    /**
     * Provides access to the primary cache.
     *
     * @return the primary cache instance.
     */
    @Override
    public Cache<String, Object> primaryCache() {
        return primaryCache;
    }

    /**
     * Retrieves the second-level cache which is implemented using Redis.
     *
     * @return a RedisTemplate instance configured for caching operations with keys of type String
     *         and values of type CacheWrapper.
     */
    @Override
    public RedisTemplate<String, CacheWrapper> secondCache() {
        return secondCache;
    }
}
