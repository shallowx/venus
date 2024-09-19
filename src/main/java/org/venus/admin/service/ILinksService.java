package org.venus.admin.service;

import org.springframework.data.repository.query.Param;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksEntity;

import java.util.List;

/**
 * Interface defining the service layer for managing links.
 * Provides methods for CRUD operations on links, including listing,
 * retrieving, adding, updating, and deleting link entries.
 */
public interface ILinksService {
    /**
     * Retrieves a list of all link entities from the system.
     *
     * @return A list containing all instances of {@link LinksEntity}.
     */
    List<LinksEntity> lists();

    /**
     * Retrieves a link entity based on its unique identifier.
     *
     * @param id the unique identifier of the link to be retrieved
     * @return the LinksEntity associated with the given id
     */
    LinksEntity get(long id);

    /**
     * Adds a new link represented by the given LinksDao object.
     *
     * @param ld The LinksDao object containing data about the link to be added.
     */
    void add(LinksDao ld);

    /**
     * Updates an existing link entry in the database using the provided LinksDao object.
     *
     * @param ld The LinksDao object containing updated data for the link to be modified.
     */
    void update(@Param("ld") LinksDao ld);

    /**
     * Deletes a link entity from the system by its unique identifier.
     *
     * @param id The unique identifier of the link entity to be deleted.
     */
    void delete(long id);
}
