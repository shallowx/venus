package org.venus.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VenusMultiLevelCache {
    String cacheName();

    String key();

    MultiLevelCacheType type() default MultiLevelCacheType.ALL;
}
