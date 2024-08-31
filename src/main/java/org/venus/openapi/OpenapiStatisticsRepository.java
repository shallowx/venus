package org.venus.openapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OpenapiStatisticsRepository extends JpaRepository<OpenapiStatisticsEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO links (link_id, clicked_at, ip, user_agent, referer) VALUES (:#{#entity.linkId}, :#{#entity.clickedAt}, :#{#entity.ip}, :#{#entity.userAgent}, :#{#entity.referer})", nativeQuery = true)
    void report(@Param("entity") OpenapiStatisticsEntity entity);
}
