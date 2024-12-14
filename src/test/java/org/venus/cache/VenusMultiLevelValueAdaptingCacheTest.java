package org.venus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the `VenusMultiLevelValueAdaptingCache` class.
 * This test class contains methods to verify the functionality and correctness of the
 * `VenusMultiLevelValueAdaptingCache` implementation, particularly focusing on lookup,
 * get, put, and evict operations in a multi-level caching system.
 */
@SuppressWarnings("all")
public class VenusMultiLevelValueAdaptingCacheTest {

    /**
     * Tests the `lookup` method of `VenusMultiLevelValueAdaptingCache` to verify that a `CacheWrapper`
     * object can be successfully retrieved from the primary cache.
     * This method performs the following steps:
     * 1. Mocks the primary cache and the secondary Redis cache.
     * 2. Sets up the `VenusMultiLevelCacheProperties` with the property `allowNull` set to true.
     * 3. Instantiates the `VenusMultiLevelValueAdaptingCache` with the provided caches and properties.
     * 4. Mocks the primary cache to return a predefined `CacheWrapper` object when queried with any key.
     * 5. Invokes the `lookup` method on the multi-level cache with the specified key.
     * 6. Asserts that the result is not null, is an instance of `CacheWrapper`, and equals the expected `CacheWrapper`.
     */
    @Test
    void testLookupCacheWrapperFromPrimaryCache() {
        String key = "key1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache =
                new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);
        CacheWrapper expectedWrapper = new CacheWrapper((String) key, "Value");

        // Mock primaryCache to return the CacheWrapper
        doReturn(expectedWrapper).when(primaryCache).getIfPresent(any());

        Object result = multiLevelCache.lookup(key);

        assertNotNull(result);
        assertInstanceOf(CacheWrapper.class, result);
        assertEquals(expectedWrapper, result);
    }

    /**
     * Tests the functionality of `lookup` method within the `VenusMultiLevelValueAdaptingCache`
     * class. The test specifically verifies that when a cache miss occurs in the primary cache,
     * the method correctly retrieves the `CacheWrapper` object from the secondary cache.
     * The setup involves:
     * 1. Creating a mock primary cache and a mock RedisTemplate as the secondary cache.
     * 2. Setting up properties to allow null values.
     * 3. Preparing the expected `CacheWrapper` object that is to be retrieved from the secondary cache.
     * 4. Mocking the primary cache to return null, indicating a cache miss.
     * 5. Mocking the secondary cache to return the expected `CacheWrapper` object.
     * 6. Invoking the `lookup` method and asserting that the result matches the expected `CacheWrapper` object.
     * Test assertions:
     * - The result is not null.
     * - The result is an instance of `CacheWrapper`.
     * - The result equals the expected `CacheWrapper` object.
     */
    @Test
    void testLookupCacheWrapperFromSecondCache() {
        String key = "key1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache =
                new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);
        CacheWrapper expectedWrapper = new CacheWrapper(key, "Value");

        // Mock primaryCache to return null and secondCache to return the CacheWrapper
        doReturn(null).when(primaryCache).getIfPresent(any());
        ValueOperations valueOperations = Mockito.mock(ValueOperations.class);
        doReturn(valueOperations).when(secondCache).opsForValue();
        doReturn(expectedWrapper).when(valueOperations).get(any(String.class));

        Object result = multiLevelCache.lookup(key);

        assertNotNull(result);
        assertInstanceOf(CacheWrapper.class, result);
        assertEquals(expectedWrapper, result);
    }

    /**
     * Test method to verify the behavior of the `lookup` method of `VenusMultiLevelValueAdaptingCache`
     * when no cache wrapper is found in either the primary or secondary caches.
     * This test initializes the necessary objects and mocks their behavior to simulate the scenario
     * where both the primary and secondary caches return null for a given key. It then asserts
     * that the result of the `lookup` method is null, confirming the expected behavior.
     * Steps performed in this test:
     * 1. Mock instances of primary and secondary caches are created.
     * 2. `VenusMultiLevelCacheProperties` is configured to allow null values.
     * 3. A `VenusMultiLevelValueAdaptingCache` instance is created with the mocked caches and properties.
     * 4. Mock behavior is set up for both caches to return null for any key.
     * 5. The `lookup` method is called with a specific key.
     * 6. The result is asserted to be null.
     */
    @Test
    void testLookupNoCacheWrapperInBothCaches() {
        String key = "key1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache =
                new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);

        // Mock primaryCache and secondCache to return null
        doReturn(null).when(primaryCache).getIfPresent(any());
        ValueOperations valueOperations = Mockito.mock(ValueOperations.class);
        doReturn(valueOperations).when(secondCache).opsForValue();
        doReturn(null).when(valueOperations).get(any(String.class));
        Object result = multiLevelCache.lookup(key);

        // Assertions
        assertNull(result);
    }
    /**
     * Tests the behavior of the `VenusMultiLevelValueAdaptingCache` when a cache hit occurs in the primary cache.
     * This test verifies that the value is correctly retrieved from the primary cache when it is present.
     * It mocks the primary cache and secondary cache, uses a sample key-value pair, and confirms that
     * the returned value matches the expected value.
     *
     * @throws Exception if an unexpected error occurs during the test execution
     */
    @Test
    void testGetWithHitInCache() throws Exception {
        String key = "key1";
        String value = "value1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache = new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);
        Callable<String> valueLoader = () -> value;

        // Mocking to return a CacheWrapper with the expected value
        doReturn(new CacheWrapper(key, value)).when(primaryCache).getIfPresent(any());
        String result = multiLevelCache.get(key, valueLoader);

        assertNotNull(result);
        assertEquals(value, result);
    }

    /**
     * Tests the cache behavior when there is a cache miss in the primary cache.
     * <br>
     * This test ensures that when a key is not present in the primary cache,
     * the `multiLevelCache` will invoke the provided value loader to retrieve the value.
     * The retrieved value is then asserted to be not null and equal to the expected value.
     *
     * @throws Exception if any error occurs during the test execution.
     */
    @Test
    void testGetWithMissInCache() throws Exception {
        String key = "key1";
        String value = "value1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache = new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);
        Callable<String> valueLoader = () -> value;

        // Mocking to return null indicating a cache miss
        doReturn(null).when(primaryCache).getIfPresent(any());
        ValueOperations<String, CacheWrapper> valueOperations = mock(ValueOperations.class);
        when(secondCache.opsForValue()).thenReturn(valueOperations);
        String result = multiLevelCache.get(key, valueLoader);

        assertNotNull(result);
        assertEquals(value, result);
    }

    /**
     * Tests the `put` method of the `VenusMultiLevelValueAdaptingCache` class when the value to be put is not null.
     * <br>
     * This test ensures that the given key-value pair is stored in the primary cache.
     * It sets up a mock primary cache, a mock secondary cache (`RedisTemplate`),
     * and an instance of `VenusMultiLevelValueAdaptingCache` with the specified properties.
     * <br>
     * Steps:
     * 1. Initializes the key and value to be tested.
     * 2. Mocks the primary cache and secondary Redis cache.
     * 3. Configures the `VenusMultiLevelCacheProperties` to allow null values.
     * 4. Creates an instance of `VenusMultiLevelValueAdaptingCache`.
     * 5. Mocks the `ValueOperations` for the second cache's `opsForValue` method.
     * 6. Executes the `put` method on the `multiLevelCache` instance.
     * 7. Verifies that the primary cache's `put` method is invoked exactly once with the
     *    expected `CacheWrapper` containing the key and value.
     *
     * @throws UnknownHostException if an unknown host is encountered during the test
     */
    @Test
    void testPutWithValueNotNull() throws UnknownHostException {
        String key = "key1";
        String value = "value1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache = new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);

        ValueOperations<String, CacheWrapper> valueOperations = Mockito.mock(ValueOperations.class);
        doReturn(valueOperations).when(secondCache).opsForValue();
        // Execute the put method
        multiLevelCache.put(key, value);

        // Verify that put method was executed on primary cache
        Mockito.verify(primaryCache, Mockito.times(1)).put(key, new CacheWrapper(key, value));
    }

    /**
     * Verifies the behavior of the `put` method when a `null` value is provided and `null` values are not allowed by cache settings.
     * This test ensures that:
     * - When a `null` value is provided and the cache configuration has `allowNull` set to `false`:
     *   - The `put` method on the primary cache is not called.
     *   - The `set` method on the secondary cache's `ValueOperations` is not called.
     *
     * @throws UnknownHostException If an error occurs related to the host environment.
     */
    @Test
    void testPutWithNullValueAndNullNotAllowed() throws UnknownHostException {
        String key = "key1";
        String value = null;
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(false);
        MultiLevelValueAdaptingCache multiLevelCache =
                new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);

        ValueOperations valueOperations = Mockito.mock(ValueOperations.class);
        doReturn(valueOperations).when(secondCache).opsForValue();

        // Execute the put method
        multiLevelCache.put(key, value);

        // Verify that put method was not executed on primary cache
        Mockito.verify(primaryCache, Mockito.times(0)).put(key, new CacheWrapper(key, value));
        // Verify that set method was not executed on value operations
        Mockito.verify(valueOperations, Mockito.times(0)).set(any(String.class), any(CacheWrapper.class));
    }

    /**
     * Tests the {@code evict(String key)} method of the {@code VenusMultiLevelValueAdaptingCache} class using a valid key.
     * This test verifies that when a key is evicted from a multi-level cache, the key is properly removed from both the
     * primary and secondary caches.
     *
     * @throws UnknownHostException if there is an error initializing the mock environment for the test
     */
    @Test
    void testEvictValidKey() throws UnknownHostException {
        String key = "key1";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache =
                new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);

        Environment environment = mock(Environment.class);
        ListenerSourceSupport.initialize(environment);
        when(ListenerSourceSupport.getSourceAddress()).thenReturn("127.0.0.1");
        multiLevelCache.evict(key);

        // Verify that evict method was executed on primary cache
        Mockito.verify(primaryCache, Mockito.times(1)).invalidate(key);
        // Verify that delete method was executed on the second cache
        Mockito.verify(secondCache, Mockito.times(1)).delete(multiLevelCache.getName() + ":" + key);
    }

    /**
     * Tests the eviction of a cache entry with an invalid key in the multi-level cache system.
     * The test verifies that attempting to evict an entry using an invalid key ('key2'):
     * - Invokes the `invalidate` method on the primary cache.
     * - Invokes the `delete` method on the secondary Redis cache with a prefixed key.
     */
    @Test
    void testEvictInvalidKey() {
        String key = "key2";
        Cache<String, Object> primaryCache = Mockito.mock(Cache.class);
        RedisTemplate<String, CacheWrapper> secondCache = Mockito.mock(RedisTemplate.class);
        MultiLevelCacheProperties properties = new MultiLevelCacheProperties();
        properties.setAllowNull(true);
        MultiLevelValueAdaptingCache multiLevelCache =
                new MultiLevelValueAdaptingCache("testCache", secondCache, primaryCache, properties);

        multiLevelCache.evict(key);
        // Verify that evict method was executed on primary cache
        Mockito.verify(primaryCache, Mockito.times(1)).invalidate(key);
        // Verify that delete method was executed on the second cache
        Mockito.verify(secondCache, Mockito.times(1)).delete(multiLevelCache.getName() + ":" + key);
    }

}
