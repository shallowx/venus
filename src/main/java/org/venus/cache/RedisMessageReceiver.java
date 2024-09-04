package org.venus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.UnknownHostException;

@Slf4j
public class RedisMessageReceiver {
    private final RedisTemplate<String, CacheListenerMessage> redisTemplate;
    private final VenusMultiLevelCacheManager manager;

    public RedisMessageReceiver(RedisTemplate<String, CacheListenerMessage> redisTemplate, VenusMultiLevelCacheManager manager) {
        this.redisTemplate = redisTemplate;
        this.manager = manager;
    }

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
                log.debug("The own service sends messages without updating the cache.");
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
