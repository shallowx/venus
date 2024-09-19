package org.venus.admin.service;

import org.venus.admin.domain.GeoEntity;

import java.util.List;

/**
 * Interface for geographical services.
 *
 * Provides methods to retrieve lists of geographical entities and details
 * of a specific geographical entity.
 */
public interface IGeoService {
    /**
     * Retrieves a list of geographical entities.
     *
     * @return a list of GeoEntity objects representing various geographical entities
     */
    List<GeoEntity> lists();

    /**
     * Retrieves the details of a geographical entity by its ID.
     *
     * @param id the ID of the geographical entity to retrieve
     * @return the geographical entity corresponding to the provided ID
     */
    GeoEntity detail(long id);
}
