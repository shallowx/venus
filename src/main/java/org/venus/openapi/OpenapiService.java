package org.venus.openapi;

import jakarta.annotation.PostConstruct;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.venus.cache.VenusMultiLevelCacheConstants.VENUS_REDIRECT_CACHE_NAME;
import static org.venus.cache.VenusMultiLevelCacheConstants.VENUS_CACHE_CALLBACK_NAME;

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
    private final VenusMultiLevelCacheManager manager;
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
     * Constructs an OpenapiService with specified dependencies.
     *
     * @param openapiRepository the repository to manage OpenAPI entities
     * @param manager the multi-level cache manager
     * @param properties the properties for OpenAPI initialization
     * @param provider the object provider for cache consistent alarm
     * @param redisTemplate the Redis template for cache operations
     */
    @Autowired
    public OpenapiService(OpenapiRepository openapiRepository, VenusMultiLevelCacheManager manager, OpenapiInitializerProperties properties,
                          ObjectProvider<OpenapiCacheConsistentAlarm> provider, RedisTemplate<String, CacheWrapper> redisTemplate) {
        this.openapiRepository = openapiRepository;
        this.manager = manager;
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.alarm = provider.getIfAvailable();
    }

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

        List<OpenapiEntity> entities = openapiRepository.lists();
        if (entities == null || entities.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("Venus redis initialize from db is empty");
            }
            return;
        }

        Cache cache = manager.getCache(VENUS_REDIRECT_CACHE_NAME);
        for (OpenapiEntity entity : entities) {
            try {
                cache.put(entity.getCode(), entity);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Venus redis key[{}] initialize was failure with entity[{}]", entity.getCode(), entity, e);
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
    @VenusMultiLevelCache(cacheName = VENUS_REDIRECT_CACHE_NAME, key = "#encode", type = MultiLevelCacheType.ALL)
    @Override
    public OpenapiEntity get(String encode) {
        return openapiRepository.get(encode);
    }


    /**
     * Retrieves a list of OpenapiEntity objects that are active and have not expired.
     * The method filters the entities based on their 'isActive' status being non-zero
     * and their 'expiresAt' timestamp being after the current date and time.
     *
     * @return a list of active and non-expired OpenapiEntity objects
     */
    @Override
    public List<OpenapiEntity> lists() {
        return openapiRepository.lists().stream()
                .filter(f -> f.getIsActive() != 0 && f.getExpiresAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    /**
     * Redirects to an OpenapiEntity based on the given encode parameter.
     *
     * @param encode the encoded string used to lookup the OpenapiEntity.
     * @return the OpenapiEntity if found and is active and not expired, otherwise null.
     */
    @VenusMultiLevelCache(cacheName = VENUS_REDIRECT_CACHE_NAME, key = "#encode", type = MultiLevelCacheType.ALL)
    @Override
    public OpenapiEntity redirect(String encode) {
        OpenapiEntity entity = this.get(encode);
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
            CacheSelector selector = (VenusMultiLevelValueAdaptingCache) manager.getCache(VENUS_REDIRECT_CACHE_NAME);
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
}
