package org.venus.admin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.venus.admin.entity.Links;

@Repository
public interface LinksRepository extends JpaRepository<Links, Long> {

    @Query(value = "SELECT l FROM Links l")
    List<Links> lists();

    @Query(value = "SELECT l FROM Links l WHERE l.id = :id")
    Links findLinksById(long id);
}
