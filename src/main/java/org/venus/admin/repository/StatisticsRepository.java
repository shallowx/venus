package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.venus.admin.domain.StatisticsEntity;

import java.util.List;

/**
 * Repository interface for accessing statistics data from the database.
 * Extends the {@link JpaRepository} to provide CRUD operations and custom queries
 * for {@link StatisticsEntity}.
 */
@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {
    /**
     * Retrieves a list of all statistics entities from the database.
     *
     * @return a list of {@link StatisticsEntity} objects containing all records from the statistics table.
     */
    @Query(value = "SELECT * FROM statistics", nativeQuery = true)
    List<StatisticsEntity> lists();

    /**
     * Retrieves a statistics entity from the database by its unique identifier.
     *
     * @param id the unique identifier of the statistics entity
     * @return the statistics entity with the specified id
     */
    @Query(value = "SELECT * FROM statistics WHERE id=:id", nativeQuery = true)
    StatisticsEntity detail(@Param("id") long id);
}
