package org.venus.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.GeoEntity;
import org.venus.admin.repository.GeoRepository;
import org.venus.admin.service.IGeoService;

import java.util.List;

@Service
@Slf4j
public class GeoService implements IGeoService {

    @Autowired
    private GeoRepository geoRepository;

    @Override
    public List<GeoEntity> lists() {
        return geoRepository.lists();
    }

    @Override
    public GeoEntity detail(long id) {
        return geoRepository.detail(id);
    }
}
