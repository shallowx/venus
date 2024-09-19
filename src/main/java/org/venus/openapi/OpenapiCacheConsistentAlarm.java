package org.venus.openapi;

/**
 * Interface representing an alarm mechanism for signaling inconsistencies
 * in an OpenAPI cache.
 *
 * The OpenapiCacheConsistentAlarm interface defines a method for generating
 * an alarm when a cache inconsistency is detected. Implementations of this
 * interface should handle the alarm process, potentially logging the inconsistency
 * or notifying relevant systems or personnel.
 */
public interface OpenapiCacheConsistentAlarm {
    /**
     * Generates an alarm when a cache inconsistency is detected.
     *
     * @param key  A unique identifier for the alarm.
     * @param o    The object associated with the alarm.
     * @param type The type/category of the alarm.
     */
    void alarm(String key, Object o, String type);
}
