package org.venus.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.venus.multi.level.cache")
public class VenusMultiLevelCacheProperties {
    private boolean allowNull = true;
    private int initCapacity = 1000;
    private int maxCapacity = Integer.MIN_VALUE;
    private long expireAfterWrite;
    private long expireAfterAccess;
    private long redisExpires;
    private long redisScanCount;
}
