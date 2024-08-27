package org.venus.admin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.StatisticsEntity;
import org.venus.admin.repository.StatisticsRepository;
import org.venus.admin.service.IStatisticsService;

import java.util.List;

@Service
@Slf4j
public class StatisticsService implements IStatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public List<StatisticsEntity> lists() {
        return statisticsRepository.lists();
    }

    @Override
    public StatisticsEntity detail(long id) {
        return statisticsRepository.detail(id);
    }
}
