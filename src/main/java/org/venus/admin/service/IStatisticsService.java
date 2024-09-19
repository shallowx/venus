package org.venus.admin.service;

import org.springframework.data.repository.query.Param;
import org.venus.admin.domain.StatisticsEntity;

import java.util.List;

/**
 * Service interface for handling statistics-related operations.
 *
 * This service provides methods for retrieving statistics data,
 * including obtaining a list of all statistics and fetching details of a specific statistic.
 */
public interface IStatisticsService {
    /**
     * Retrieves a list of all statistics entries.
     *
     * @return a list of {@link StatisticsEntity} instances representing all the statistics entries
     */
    List<StatisticsEntity> lists();

    /**
     * Fetches the details of a specific statistic based on the provided unique identifier.
     *
     * @param id the unique identifier of the statistic to retrieve
     * @return the {@link StatisticsEntity} containing detailed information of the specified statistic
     */
    StatisticsEntity detail(@Param("id") long id);
}
