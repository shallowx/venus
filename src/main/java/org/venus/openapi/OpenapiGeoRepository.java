package org.venus.openapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OpenapiGeoRepository extends JpaRepository<OpenapiGeoEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO geo (click_id, country, city, latitude, longitude) VALUES (:#{#entity.clickId}, :#{#entity.country}, :#{#entity.city}, :#{#entity.lat}, :#{#entity.lng})", nativeQuery = true)
    void report(@Param("entity") OpenapiGeoEntity entity);
}
