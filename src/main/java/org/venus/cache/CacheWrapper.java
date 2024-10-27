package org.venus.cache;

import lombok.*;
import org.springframework.data.annotation.Immutable;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A class that represents a wrapper for cached data, consisting of a key and its associated value.
 * This class provides functionality for constructing cache entries, checking equality, computing
 * hash codes, and generating string representations of the cache entries.
 */
@Immutable
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
    @Getter
    @Setter
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
    @Getter
    @Setter
    private int hash;

    /**
     * A constant placeholder object used to represent a null value.
     * This can be used in collections or other data structures where
     * null values need a stand-in to avoid null checks or NullPointerExceptions.
     */
    private static final Object NULL_VALUE = new Object();

    /**
     * Default constructor for CacheWrapper.
     * Initializes a new instance of CacheWrapper with no key or value set.
     */
    private CacheWrapper() {
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
        this.value = maskNullValues(value);
        this.hash = 31 * (1 + 31 * key.hashCode()) + value.hashCode();
    }

    /**
     * Retrieves the stored value after unmasking any predefined null values.
     *
     * @return the unmasked value stored in the cache.
     */
    public Object getValue() {
        return unmaskNullValues(value);
    }

    /**
     * Sets the value for the cache wrapper.
     *
     * This method assigns a masked version of the input value to the instance variable.
     *
     * @param value The new value to be set, which will be masked if null.
     */
    public void setValue(Object value) {
        this.value = maskNullValues(value);
    }

    /**
     * Masks null values with a predefined constant.
     *
     * This method checks if the input value is null. If it is, the method returns
     * a constant representing null values. If the value is not null, it returns
     * the original value.
     *
     * @param value The input value to be checked and potentially masked if null.
     * @return The original value if it is not null, otherwise a constant used to
     * represent null values.
     */
    private Object maskNullValues(Object value) {
        return value == null ? NULL_VALUE : value;
    }

    /**
     * This method unmaskNullValues checks if the given value matches
     * the predefined NULL_VALUE and returns null if it does. Otherwise,
     * it returns the original value.
     *
     * @param value the value to check for the predefined NULL_VALUE
     *              placeholder.
     * @return the original value if it is not the predefined NULL_VALUE,
     *         null otherwise.
     */
    public static Object unmaskNullValues(Object value) {
        return value == NULL_VALUE ? null : value;
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


    @Serial
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(key);
        out.writeObject(value);
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String key = (String) in.readObject();
        Object o = in.readObject();
        this.value = unmaskNullValues(o);
        this.key = key;
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
