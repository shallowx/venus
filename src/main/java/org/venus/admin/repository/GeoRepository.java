package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.venus.admin.domain.GeoEntity;

import java.util.List;

/**
 * GeoRepository is a Spring Data JPA repository for managing GeoEntity objects.
 *
 * This repository provides CRUD operations and custom query methods to interact with the "geo" table in the database.
 */
@Repository
public interface GeoRepository extends JpaRepository<GeoEntity, Long> {
    /**
     * Retrieves all GeoEntity records from the "geo" table in the database.
     *
     * @return A list of all GeoEntity objects retrieved from the database.
     */
    @Query(value = "SELECT * FROM geo", nativeQuery = true)
    List<GeoEntity> lists();

    /**
     * Executes a native SQL query to retrieve a GeoEntity object from the "geo" table by its unique identifier.
     *
     * This method uses a @Query annotation to define the SQL query and its parameters,
     * and is mapped to the corresponding method in the GeoRepository interface.
     *
     * @param id The unique identifier of the GeoEntity to retrieve.
     * @return A GeoEntity object with the specified ID, or null if no such entity exists.
     */
    @Query(value = "SELECT * FROM geo WHERE id=:id", nativeQuery = true)
    GeoEntity detail(@Param("id") long id);
}
