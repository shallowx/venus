package org.venus.admin.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for configuring the primary datasource.
 *
 * This class is used to bind properties defined with the prefix "spring.venus.datasource.default"
 * to configure the datasource that serves as the default target in a dynamic data source setup.
 *
 * The properties include:
 * - driverClassName: The fully qualified class name of the JDBC driver.
 * - url: The URL of the datasource.
 * - username: The username for connecting to the datasource.
 * - password: The password for connecting to the datasource.
 *
 * These properties are essential for initializing a datasource instance which can be dynamically
 * managed and switched during runtime.
 *
 * This class is typically used in combination with the DynamicDataSource setup to enable switching
 * between different datasources dynamically.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.venus.datasource.default")
public class DatasourceProperties {
    /**
     * The fully qualified class name of the JDBC driver.
     *
     * This property is part of the configuration properties for the primary datasource.
     * It specifies the driver that should be used to establish a connection to the database.
     */
    private String driverClassName;
    /**
     * The URL of the primary datasource.
     *
     * This URL is used to establish a connection to the primary datasource,
     * which serves as the default target in a dynamic data source setup.
     */
    private String url;
    /**
     * The username for connecting to the datasource.
     */
    private String username;
    /**
     * The password for connecting to the datasource.
     */
    private String password;
}
