package org.venus.openapi;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OpenAPI reports.
 *
 * This class contains nested configurations for geo and statistics reports.
 * It is primarily used for setting up the scheduled tasks and report parameters.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "spring.venus.openapi.report")
public class OpenapiReportProperties {
    /**
     * Configuration properties for geo reports.
     *
     * This object holds configuration values for geo-related reports,
     * including scheduled delays, periods, timeouts, and report size.
     */
    private GeoReportProperties geo;
    /**
     * Configuration properties for the statistics report.
     *
     * This field holds the configuration settings specific to generating and scheduling
     * statistics reports. It includes parameters for scheduled delays, periods, timeouts,
     * and report sizes.
     */
    private StatisticsReportProperties statistics;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class GeoReportProperties {
        /**
         * Delay in milliseconds before the scheduled task is executed.
         */
        private long scheduledDelay;
        /**
         * Represents the period at which a scheduled task or report is repeated.
         */
        private long ScheduledPeriod;
        /**
         * The timeout duration for generating a report, in milliseconds.
         * This variable determines the maximum amount of time to wait before
         * the report generation process is canceled.
         */
        private long reportTimeout;
        /**
         * The duration in milliseconds after which a report is considered outdated if no new data is received.
         */
        private long reportWithoutDataTimeout;
        /**
         * The maximum size of the report in bytes.
         */
        private long reportSize;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class StatisticsReportProperties {
        /**
         * The delay in milliseconds before the first execution of the scheduled task.
         */
        private long scheduledDelay;
        /**
         * Represents the frequency, in milliseconds, with which a scheduled report is generated.
         */
        private long ScheduledPeriod;
        /**
         * The duration in milliseconds before a report operation times out.
         */
        private long reportTimeout;
        /**
         * The timeout period in milliseconds after which a report is generated even if no new data has been received.
         */
        private long reportWithoutDataTimeout;
        /**
         * Size of the report in bytes.
         */
        private long reportSize;
    }
}
