package org.venus.cache;

/**
 * VenusMultiLevelCacheConstants is a utility class that holds constant values
 * used across the Venus multi-tier caching system.
 *
 * This class contains constants for default names associated with various
 * components within the Venus caching infrastructure.
 */
public final class VenusMultiLevelCacheConstants {
    /**
     * Represents the default name assigned to the listener within the
     * Venus multi-tier caching system.
     */
    public static final String DEFAULT_LISTENER_NAME = "venus-default-listener";
    /**
     * The name of the cache used for storing redirect information within the
     * Venus multi-tier caching system.
     */
    public static final String VENUS_REDIRECT_CACHE_NAME = "venus-redirect";
    /**
     * VENUS_CACHE_CALLBACK_NAME is a constant representing the default name for the
     * callback function used in the Venus caching system.
     *
     * This constant is utilized within the multi-tier caching infrastructure to identify
     * the specific callback logic associated with cache operations.
     */
    public static final String VENUS_CACHE_CALLBACK_NAME = "venus-cache-callback";
}