package org.venus.cache;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A class that represents a wrapper for cached data, consisting of a key and its associated value.
 * This class provides functionality for constructing cache entries, checking equality, computing
 * hash codes, and generating string representations of the cache entries.
 */
@Getter
@Setter
public class CacheWrapper implements Serializable {
    /**
     * Serial version UID for ensuring compatibility during the deserialization process.
     * This ID is used to verify that the sender and receiver of a serialized object
     * have loaded classes for that object that are compatible with respect to serialization.
     */
    @Serial
    private static final long serialVersionUID = -3390119726919797623L;

    /**
     * Represents the key for the cache entry.
     * This key is used to uniquely identify and access the corresponding value in the cache.
     */
    private String key;
    /**
     * The value associated with the cache entry.
     * This can be any object that the cache is meant to store alongside its key.
     */
    private Object value;
    /**
     * The precomputed hash code for the cache entry, combining the hash codes of the key and value.
     * This value is calculated when a CacheWrapper object is instantiated and is used in the
     * hashCode() method to efficiently retrieve the hash code for the cache entry.
     */
    private int hash;

    /**
     * Default constructor for CacheWrapper.
     * Initializes a new instance of CacheWrapper with no key or value set.
     */
    public CacheWrapper() {
    }

    /**
     * Constructs a new CacheWrapper with the specified key and value,
     * then computes and stores the hash code based on these two parameters.
     *
     * @param key the key associated with the cached data
     * @param value the value associated with the cached data
     */
    public CacheWrapper(String key, Object value) {
        this.key = key;
        this.value = value;
        this.hash = Objects.hash(key,value);
    }

    /**
     * Compares this CacheWrapper to the specified object for equality.
     *
     * @param o the object to compare this CacheWrapper against
     * @return {@code true} if the specified object is equal to this CacheWrapper; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheWrapper wrapper = (CacheWrapper) o;
        return Objects.equals(key, wrapper.key) && Objects.equals(value, wrapper.value);
    }

    /**
     * Returns the hash code value for this cache wrapper.
     * The hash code is precomputed and stored during the construction of the object.
     *
     * @return the precomputed hash code value for this cache wrapper
     */
    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * Generates a string representation of the CacheWrapper containing the key and value.
     *
     * @return A string that represents the CacheWrapper instance, including its key and value fields.
     */
    @Override
    public String toString() {
        return "CacheWrapper{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
