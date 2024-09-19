package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksEntity;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on the LinksEntity.
 * Extends JpaRepository for additional JPA functionalities.
 */
@Repository
public interface LinksRepository extends JpaRepository<LinksEntity, Long> {
    /**
     * Retrieves all LinkEntity records from the "links" table.
     *
     * @return A list of all LinkEntity objects present in the database.
     */
    @Query(value = "SELECT * FROM links", nativeQuery = true)
    List<LinksEntity> list();

    /**
     * Retrieves a LinksEntity from the database based on the provided unique identifier.
     *
     * @param id The unique identifier of the LinksEntity.
     * @return The LinksEntity with the specified id.
     */
    @Query(value = "SELECT * FROM links WHERE id=:id", nativeQuery = true)
    LinksEntity get(@Param("id") long id);

    /**
     * Adds a new record to the links table.
     *
     * @param ld Data object containing the details of the link to be added.
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO links (id, code, redirect, original_url, expires_at, is_active) VALUES (:#{#ld.id}, :#{#ld.code}, :#{#ld.redirect}, :#{#ld.originalUrl}, :#{#ld.expiresAt}, :#{#ld.isActive})", nativeQuery = true)
    void add(@Param("ld") LinksDao ld);

    /**
     * Updates an existing link record in the database with new data provided in the LinksDao object.
     *
     * @param ld the LinksDao object containing the updated data for the link
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE links SET code = :#{#ld.code}, redirect = :#{#ld.redirect}, original_url = :#{#ld.originalUrl}, expires_at = :#{#ld.expiresAt}, is_active = :#{#ld.isActive} WHERE id = :#{#ld.id}", nativeQuery = true)
    void update(@Param("ld") LinksDao ld);

    /**
     * Deletes a link from the database based on the provided id.
     *
     * @param id the unique identifier of the link to be deleted
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM links where id=:id", nativeQuery = true)
    void remove(@Param("id") long id);
}
