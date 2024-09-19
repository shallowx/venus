package org.venus.openapi;

/**
 * Provides services for reporting OpenAPI statistics.
 */
public interface IOpenapiStatisticsReportService {
    /**
     * Reports the OpenAPI statistics.
     *
     * @param entity the OpenapiStatisticsEntity containing the details of the statistics to be reported
     */
    void report(OpenapiStatisticsEntity entity);
}
