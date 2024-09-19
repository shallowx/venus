package org.venus.cache;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a message used for cache notifications.
 * This class is typically used in scenarios where cache synchronization or invalidation
 * needs to be communicated across different components or systems.
 *
 * An instance of this class holds the necessary information to identify the cache,
 * the key affected, the type of cache action, the source of the message, and the associated value.
 *
 * Annotations:
 * - Lombok annotations are used to auto-generate constructor, builder, setters, getters,
 *   and equals/hashCode methods.
 * - Implements Serializable interface for object serialization.
 *
 * Fields:
 * - name: The cache name to which this message is relevant.
 * - type: The type of cache message action (e.g., update, invalidate).
 * - key: The specific cache key that is affected by this message.
 * - value: The value to be associated with the cache key (typically used for updates).
 * - source: The source of the message, used to identify the origin of the cache change.
 *
 * Overrides:
 * - toString: Provides a string representation of the object for logging or debugging purposes.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@EqualsAndHashCode
public class CacheListenerMessage implements Serializable {

    /**
     * A unique identifier used during the deserialization process to verify that the sender
     * and receiver of a serialized object have compatible versions of the class.
     * This field helps to ensure that a serialized object can be correctly deserialized
     * even if the class definition has changed between serialization and deserialization.
     */
    @Serial
    private static final long serialVersionUID = -2385126650426135507L;

    /**
     * The cache name to which this message is relevant.
     */
    private String name;
    /**
     * The type of cache message action.
     *
     * This variable indicates the type of cache action to be performed, such as an update or invalidation.
     */
    private CacheMessageListenerType type;
    /**
     * The specific cache key that is affected by the cache message.
     *
     * This field holds the identifier for a particular cache entry that is relevant
     * for the cache synchronization or invalidation message.
     */
    private String key;
    /**
     * The value to be associated with the cache key.
     * Typically used for updates to indicate the new value to be set.
     * It can be any object that represents the data to be stored or manipulated in the cache.
     */
    private Object value;
    /**
     * The source of the cache message, used to identify the origin of the cache change.
     */
    private String source;

    /**
     * Returns a string representation of the CacheListenerMessage object.
     *
     * @return A string that contains the values of the name, type, key, value, and source fields.
     */
    @Override
    public String toString() {
        return "CacheListenerMessage{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", source='" + source + '\'' +
                '}';
    }
}
