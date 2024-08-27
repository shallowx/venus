package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.venus.admin.domain.StatisticsEntity;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {
    @Query(value = "SELECT * FROM statistics", nativeQuery = true)
    List<StatisticsEntity> lists();

    @Query(value = "SELECT * FROM statistics WHERE id=:id", nativeQuery = true)
    StatisticsEntity detail(@Param("id") long id);
}
