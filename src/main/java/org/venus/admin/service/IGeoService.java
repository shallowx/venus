package org.venus.admin.service;

import org.venus.admin.domain.GeoEntity;

import java.util.List;

public interface IGeoService {
    List<GeoEntity> lists();

    GeoEntity detail(long id);
}
