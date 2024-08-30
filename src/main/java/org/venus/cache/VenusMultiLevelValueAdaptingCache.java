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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.venus.cache.VenusMultiLevelCacheConstants.DEFAULT_LISTENER_NAME;


@Slf4j
public class VenusMultiLevelValueAdaptingCache extends AbstractValueAdaptingCache {
    private String cacheName;
    private RedisTemplate<String,CacheWrapper> secondCache;
    private Cache<String, CacheWrapper> primaryCache;
    private VenusMultiLevelCacheProperties properties;

    public VenusMultiLevelValueAdaptingCache(String cacheName, RedisTemplate<String, CacheWrapper> template, Cache<String, CacheWrapper> primaryCache, VenusMultiLevelCacheProperties properties) {
        super(properties.isAllowNull());
        this.cacheName = cacheName;
        this.secondCache = template;
        this.primaryCache = primaryCache;
        this.properties = properties;
    }

    protected VenusMultiLevelValueAdaptingCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    protected Object lookup(@NonNull Object key) {
        CacheWrapper wrapper = primaryCache.getIfPresent((String) key);
        if (wrapper != null) {
            if (log.isDebugEnabled()) {
                log.debug("Get data from primary cache");
            }
            return wrapper;
        }

        String redisKey = buildKey(key);
        wrapper = secondCache.opsForValue().get(redisKey);
        if (wrapper != null) {
            if (log.isDebugEnabled()) {
                log.debug("Get data from second cache");
            }
            primaryCache.put((String) key, wrapper);
        }
        return wrapper;
    }

    @NonNull
    @Override
    public String getName() {
        return this.cacheName;
    }

    @NonNull
    @Override
    public Object getNativeCache() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        try {
            CacheWrapper wrapper = (CacheWrapper)lookup(key);
            if (wrapper != null) {
                return (T) wrapper.getValue();
            }
            T t = valueLoader.call();
            synchronized (this) {
                wrapper = (CacheWrapper)lookup(key);
                if (wrapper != null) {
                    return (T) wrapper.getValue();
                } else {
                    put(key, t);
                }
            }
            return t;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void put(@NonNull Object key, Object value) {
        if (!isAllowNullValues() && Objects.isNull(value)) {
            if (log.isWarnEnabled()) {
                log.warn("The value NULL will not be cached");
            }
            return;
        }
        primaryCache.put((String) key, CacheWrapper.builder().key((String) key).value(toStoreValue(value)).build());

        String redisKey = buildKey(key);
        Optional<Long> expireOpt = Optional.ofNullable(properties)
                .map(VenusMultiLevelCacheProperties::getRedisExpires);
        if (expireOpt.isPresent()) {
            secondCache.opsForValue().set(redisKey, CacheWrapper.builder().key(redisKey).value(value).build(), expireOpt.get(), TimeUnit.MILLISECONDS);
        } else {
            secondCache.opsForValue().set(redisKey, CacheWrapper.builder().key(redisKey).value(value).build());
        }

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

    @Override
    public void clear() {
        Set<String> keys = getCacheKeys(this.cacheName.concat(":*"));
        secondCache.delete(keys);
        primaryCache.invalidateAll();
    }

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

    private String buildKey(Object key) {
        return this.cacheName + ":" + key;
    }
}
