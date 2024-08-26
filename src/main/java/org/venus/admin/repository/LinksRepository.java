package org.venus.admin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.venus.admin.domain.Links;
import org.venus.admin.domain.LinksDao;

@Repository
public interface LinksRepository extends JpaRepository<Links, Long> {

    @Query(value = "SELECT * FROM links", nativeQuery = true)
    List<Links> list();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO links (id, code, redirect, original_url, expires_at, is_active) VALUES (:#{#ld.id}, :#{#ld.code}, :#{#ld.redirect}, :#{#ld.originalUrl}, :#{#ld.expiresAt}, :#{#ld.isActive})", nativeQuery = true)
    void add(@Param("ld") LinksDao ld);
}
