package org.venus.admin.dal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.venus.admin.entiy.Statistics;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    @Query(value = "SELECT s FROM Statistics s")
    List<Statistics> lists();

    @Query(value = "SELECT s FROM Statistics s WHERE s.id = :id")
    Statistics findStatisticsById(long id);
}
