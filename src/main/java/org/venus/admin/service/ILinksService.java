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
     * Adds a new link to the system.
     *
     * @param ld the LinksDao object containing the link data to be added
     * @return true if the link was successfully added, false otherwise
     */
    boolean add(LinksDao ld);

    /**
     * Updates an existing link entry with the data provided in the LinksDao object.
     *
     * @param ld LinksDao object containing the updated information for the link entry.
     * @return true if the update was successful, false otherwise.
     */
    boolean update(@Param("ld") LinksDao ld);

    /**
     * Deletes a link entity based on its unique identifier.
     *
     * @param id the unique identifier of the link to be deleted
     * @return true if the deletion was successful, false otherwise
     */
    boolean delete(long id);
}
