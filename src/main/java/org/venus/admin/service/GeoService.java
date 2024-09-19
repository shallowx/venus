package org.venus.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.GeoEntity;
import org.venus.admin.repository.GeoRepository;

import java.util.List;

/**
 * Service class responsible for handling geographic entities.
 *
 * This class implements the IGeoService interface and provides
 * methods to retrieve lists of geographic entities or detailed
 * information about a specific geographic entity.
 *
 * It uses the GeoRepository to interact with the underlying database.
 */
@Service
@Slf4j
public class GeoService implements IGeoService {

    /**
     * Instance of GeoRepository used to perform CRUD operations on GeoEntity objects.
     *
     * This repository is injected by Spring's dependency injection mechanism using the @Autowired annotation.
     * It provides access to methods for retrieving lists of geographical entities and details of a specific entity.
     */
    @Autowired
    private GeoRepository geoRepository;

    /**
     * Retrieves a list of geographical entities.
     *
     * @return a list of GeoEntity objects representing various geographical entities
     */
    @Override
    public List<GeoEntity> lists() {
        return geoRepository.lists();
    }

    /**
     * Retrieves detailed information about a geographic entity by its unique identifier.
     *
     * @param id the unique identifier of the geographic entity to retrieve
     * @return the geographic entity corresponding to the specified ID, or null if no such entity exists
     */
    @Override
    public GeoEntity detail(long id) {
        return geoRepository.detail(id);
    }
}
