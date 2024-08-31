package org.venus.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksEntity;

import java.util.List;

@Repository
public interface LinksRepository extends JpaRepository<LinksEntity, Long> {
    @Query(value = "SELECT * FROM links", nativeQuery = true)
    List<LinksEntity> list();

    @Query(value = "SELECT * FROM links WHERE id=:id", nativeQuery = true)
    LinksEntity get(@Param("id") long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO links (id, code, redirect, original_url, expires_at, is_active) VALUES (:#{#ld.id}, :#{#ld.code}, :#{#ld.redirect}, :#{#ld.originalUrl}, :#{#ld.expiresAt}, :#{#ld.isActive})", nativeQuery = true)
    void add(@Param("ld") LinksDao ld);

    @Modifying
    @Transactional
    @Query(value = "UPDATE links SET code = :#{#ld.code}, redirect = :#{#ld.redirect}, original_url = :#{#ld.originalUrl}, expires_at = :#{#ld.expiresAt}, is_active = :#{#ld.isActive} WHERE id = :#{#ld.id}", nativeQuery = true)
    void update(@Param("ld") LinksDao ld);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM links where id=:id", nativeQuery = true)
    void remove(@Param("id") long id);
}
