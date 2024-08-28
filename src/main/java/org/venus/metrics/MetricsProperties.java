package org.venus.metrics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.venus.metrics.prometheus")
public class MetricsProperties {
    private boolean enabled = true;
    private String scrapeUrl = "/prometheus";
    private String host = "127.0.0.1";
    private int port = 9090;
}
