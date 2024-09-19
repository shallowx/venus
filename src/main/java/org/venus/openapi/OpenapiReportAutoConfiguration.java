package org.venus.openapi;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI report generation and initialization properties.
 *
 * This class enables configuration properties from the OpenapiReportProperties and
 * OpenapiInitializerProperties classes, which provide various settings for geo and
 * statistics reports, as well as initialization settings.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenapiReportProperties.class,OpenapiInitializerProperties.class})
public class OpenapiReportAutoConfiguration {
}
