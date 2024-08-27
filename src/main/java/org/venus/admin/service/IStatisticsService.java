package org.venus.admin.service;

import org.springframework.data.repository.query.Param;
import org.venus.admin.domain.StatisticsEntity;

import java.util.List;

public interface IStatisticsService {
    List<StatisticsEntity> lists();

    StatisticsEntity detail(@Param("id") long id);
}
