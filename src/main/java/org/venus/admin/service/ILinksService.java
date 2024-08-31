package org.venus.admin.service;

import org.springframework.data.repository.query.Param;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksEntity;

import java.util.List;

public interface ILinksService {
    List<LinksEntity> lists();

    LinksEntity get(long id);

    void add(LinksDao ld);

    void update(@Param("ld") LinksDao ld);

    void delete(long id);
}
