package org.venus.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.StatisticsEntity;
import org.venus.admin.repository.StatisticsRepository;

import java.util.List;

/**
 * Service implementation for handling statistics-related operations.
 *
 * This class implements the {@link IStatisticsService} interface and provides
 * methods for retrieving statistics data including obtaining a list of all
 * statistics and fetching details of a specific statistic.
 */
@Service
@Slf4j
public class StatisticsService implements IStatisticsService {

    /**
     * Repository interface for accessing statistics data from the database.
     * Extends the {@link JpaRepository} to provide CRUD operations and custom queries
     * for {@link StatisticsEntity}.
     */
    @Autowired
    private StatisticsRepository statisticsRepository;

    /**
     * Retrieves a list of all statistics entries.
     *
     * @return a list of {@link StatisticsEntity} instances representing all the statistics entries
     */
    @Override
    public List<StatisticsEntity> lists() {
        return statisticsRepository.lists();
    }

    /**
     * Fetches the details of a specific statistic based on the provided unique identifier.
     *
     * @param id the unique identifier of the statistic to retrieve
     * @return the {@link StatisticsEntity} containing detailed information of the specified statistic
     */
    @Override
    public StatisticsEntity detail(long id) {
        return statisticsRepository.detail(id);
    }
}
