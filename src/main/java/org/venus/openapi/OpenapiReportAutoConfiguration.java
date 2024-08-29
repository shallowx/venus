package org.venus.openapi;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenapiReportProperties.class})
public class OpenapiReportAutoConfiguration {
}
