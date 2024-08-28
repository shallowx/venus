package org.venus.admin.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.venus.datasource.default")
public class DatasourceProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
