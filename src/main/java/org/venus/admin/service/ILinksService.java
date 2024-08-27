package org.venus.admin.service;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.venus.admin.domain.LinksEntity;
import org.venus.admin.domain.LinksDao;

public interface ILinksService {
    List<LinksEntity> lists();
    LinksEntity get(long id);
    void add(LinksDao ld);
    void update(@Param("ld")LinksDao ld);
    void delete(long id);
}
