package org.venus.admin.dal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.venus.admin.entiy.Geo;

@Repository
public interface GeoRepository extends JpaRepository<Geo, Long> {

    @Query(value = "SELECT g FROM Geo g")
    List<Geo> lists();

    @Query(value = "SELECT g FROM Geo g WHERE g.id = :id")
    Geo findGeoById(long id);
}
