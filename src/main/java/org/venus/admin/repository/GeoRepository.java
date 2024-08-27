package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.venus.admin.domain.GeoEntity;

import java.util.List;

@Repository
public interface GeoRepository extends JpaRepository<GeoEntity, Long> {
    @Query(value = "SELECT * FROM geo", nativeQuery = true)
    List<GeoEntity> lists();

    @Query(value = "SELECT * FROM geo WHERE id=:id", nativeQuery = true)
    GeoEntity detail(@Param("id") long id);
}
