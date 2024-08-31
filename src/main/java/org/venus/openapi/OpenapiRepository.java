package org.venus.openapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenapiRepository extends JpaRepository<OpenapiEntity, Long> {
    @Query(value = "SELECT * FROM links WHERE code=:encode", nativeQuery = true)
    OpenapiEntity get(@Param("encode") String encode);

    @Query(value = "SELECT * FROM links", nativeQuery = true)
    List<OpenapiEntity> lists();
}
