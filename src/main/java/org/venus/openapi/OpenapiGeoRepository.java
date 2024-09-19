package org.venus.openapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for managing geolocation data in the OpenAPI service.
 *
 * This interface extends the JpaRepository interface to provide basic CRUD operations
 * on OpenapiGeoEntity objects. Additionally, it includes a custom method for reporting
 * geolocation data.
 *
 * Annotations used:
 * - `@Repository`: Indicates that this interface is a Spring Data repository.
 * - `@Transactional`: Specifies that the method should be executed within a transaction.
 * - `@Modifying`: Indicates that the custom query method modifies data.
 * - `@Query`: Provides the custom SQL query for the method.
 *
 * Methods:
 * - `report`: Reports geolocation data to the database.
 */
@Repository
public interface OpenapiGeoRepository extends JpaRepository<OpenapiGeoEntity, Long> {
    /**
     * Inserts a new geolocation record into the database.
     *
     * This method is annotated to indicate that it performs a modifying query
     * within a transactional context. It takes an OpenapiGeoEntity object as a
     * parameter and inserts its properties into the corresponding columns of
     * the `geo` table.
     *
     * @param entity The OpenapiGeoEntity object containing geolocation data to be reported.
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO geo (click_id, country, city, latitude, longitude) VALUES (:#{#entity.clickId}, :#{#entity.country}, :#{#entity.city}, :#{#entity.lat}, :#{#entity.lng})", nativeQuery = true)
    void report(@Param("entity") OpenapiGeoEntity entity);
}
