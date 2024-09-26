package org.venus.openapi;

/**
 * Provides services for reporting OpenAPI statistics.
 */
public interface IOpenapiStatisticsReportService {
    /**
     * Reports the OpenAPI statistics based on the provided entity.
     *
     * @param entity the OpenapiStatisticsEntity containing the statistics information to be reported
     * @return true if the reporting was successful, false otherwise
     */
    boolean report(OpenapiStatisticsEntity entity);
}
