package org.venus.openapi;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
}
