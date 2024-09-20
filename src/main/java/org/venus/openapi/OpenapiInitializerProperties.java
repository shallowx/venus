package org.venus.openapi;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for initializing OpenAPI settings.
 *
 * This class is used to bind properties from the application's configuration files
 * with the prefix "spring.venus.openapi.initializer" to the corresponding fields
 * in this class. It includes a boolean property that indicates whether the
 * OpenAPI has been initialized.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "spring.venus.openapi.initializer")
public class OpenapiInitializerProperties {
    /**
     * Indicates whether the OpenAPI has been initialized.
     */
    private boolean initialized;
    /**
     * The duration period for checking the primary cache.
     *
     * This variable defines how frequently the primary cache should be checked.
     * It is represented as a Duration object, which provides various utilities
     * for managing time-based values.
     */
    private Duration checkPrimaryCachePeriod;
    /**
     * The initial delay before starting the OpenAPI initialization.
     *
     * This value defines the time to wait before beginning any OpenAPI
     * initialization processes. It is represented as a Duration object,
     * allowing specification in various time units.
     */
    private Duration initialDelay;
}
