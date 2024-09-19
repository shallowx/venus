package org.venus.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a method should be used as a listener for cache messages.
 *
 * This annotation is used to mark methods that are intended to process cache-related messages,
 * such as updates or invalidations. Methods annotated with this will typically be invoked
 * when a relevant cache message is received. It can be utilized in conjunction with
 * messaging frameworks to handle cache synchronization and invalidation events.
 *
 * Targets:
 * - Methods: This annotation can only be applied to method declarations.
 *
 * Retention Policy:
 * - Runtime: The annotation is retained at runtime and can be accessed through reflection.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheMessageListener {
    // only just tips
}
