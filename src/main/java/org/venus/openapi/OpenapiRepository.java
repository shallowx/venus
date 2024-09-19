package org.venus.openapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on OpenapiEntity.
 * Extends JpaRepository to leverage Spring Data JPA functionalities.
 */
@Repository
public interface OpenapiRepository extends JpaRepository<OpenapiEntity, Long> {
    /**
     * Retrieves an OpenapiEntity based on the provided code.
     *
     * @param encode the unique code to search for in the links table
     * @return an OpenapiEntity that matches the provided code
     */
    @Query(value = "SELECT * FROM links WHERE code=:encode", nativeQuery = true)
    OpenapiEntity get(@Param("encode") String encode);

    /**
     * Executes a native query to retrieve all records from the "links" table.
     *
     * @return A list of OpenapiEntity objects representing all the records in the "links" table.
     */
    @Query(value = "SELECT * FROM links", nativeQuery = true)
    List<OpenapiEntity> lists();
}
