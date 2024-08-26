package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.venus.admin.domain.Geo;

@Repository
public interface GeoRepository extends JpaRepository<Geo, Long> {

}
