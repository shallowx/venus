package org.venus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.UnknownHostException;

/**
 * RedisMessageReceiver is responsible for receiving and handling Redis messages to synchronize
 * cache entries in a multi-level cache system. It listens for cache update and invalidation messages and performs
 * the appropriate cache operations to ensure cache consistency across different cache levels.
 *
 * This class utilizes a RedisTemplate for message deserialization and a VenusMultiLevelCacheManager to manage
 * the cache operations.
 */
@Slf4j
public class RedisMessageReceiver {
    /**
     * RedisTemplate for interacting with the Redis data store.
     *
     * This*/
    private final RedisTemplate<String, CacheListenerMessage> redisTemplate;
    /**
     * The manager responsible for handling multi-level caching operations.
     *
     * VenusMultiLevelCacheManager is used to execute cache operations across different levels
     * such as local (Caffeine) and remote (Redis) caches. It helps in maintaining cache consistency
     * and performing update and invalidation operations as needed.
     */
    private final VenusMultiLevelCacheManager manager;

    /**
     * Constructor for RedisMessageReceiver.
     *
     * Initializes the RedisMessageReceiver with the provided RedisTemplate and VenusMultiLevelCacheManager.
     *
     * @param redisTemplate the RedisTemplate used for message deserialization
     * @param manager the VenusMultiLevelCacheManager used to manage cache operations
     */
    public RedisMessageReceiver(RedisTemplate<String, CacheListenerMessage> redisTemplate, VenusMultiLevelCacheManager manager) {
        this.redisTemplate = redisTemplate;
        this.manager = manager;
    }


    /**
     * Receives and processes a cache message to update or invalidate cache entries.
     * This method is triggered when a message is received, either updating or invalidating
     * a cache key based on the type of operation specified in the message.
     *
     * @param message The serialized cache message as a String. It includes information such as
     *                the cache name, key, value, and the type of cache operation (update or invalidate).
     * @throws UnknownHostException If there is an error identifying the host in cases where the message source is compared.
     */
    @CacheMessageListener
    @SuppressWarnings("ConstantConditions")
    public void receive(String message) throws UnknownHostException {
        CacheListenerMessage clm = (CacheListenerMessage) redisTemplate.getValueSerializer().deserialize(message.getBytes());
        if (clm == null) {
            if (log.isDebugEnabled()) {
                log.debug("Receive messages is NULL that it's without updating the cache.");
            }
            return;
        }

        if (clm.getSource().equals(ListenerSourceSupport.getSourceAddress())) {
            if (log.isDebugEnabled()) {
                log.debug("The own service sends messages without updating the cache[key:{}].", clm.getKey());
            }
            return;
        }

        CacheSelector selector = (VenusMultiLevelValueAdaptingCache) manager.getCache(clm.getName());
        Cache<String, Object> primaryCache = selector.primaryCache();
        if (clm.getType() == CacheMessageListenerType.UPDATE) {
            primaryCache.put(clm.getKey(), new CacheWrapper(clm.getKey(), clm.getValue()));
        }

        if (clm.getType() == CacheMessageListenerType.INVALIDATE) {
            primaryCache.invalidate(clm.getKey());
        }
    }
}
