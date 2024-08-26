package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.venus.admin.domain.Statistics;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

}
