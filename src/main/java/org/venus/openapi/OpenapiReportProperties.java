package org.venus.openapi;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "spring.venus.openapi.report")
public class OpenapiReportProperties {
    private GeoReportProperties geo;
    private StatisticsReportProperties statistics;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class GeoReportProperties {
        private long scheduledDelay;
        private long ScheduledPeriod;
        private long reportTimeout;
        private long reportWithoutDataTimeout;
        private long reportSize;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class StatisticsReportProperties {
        private long scheduledDelay;
        private long ScheduledPeriod;
        private long reportTimeout;
        private long reportWithoutDataTimeout;
        private long reportSize;
    }
}
