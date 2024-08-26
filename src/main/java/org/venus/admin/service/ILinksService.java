package org.venus.admin.service;

import java.util.List;
import org.venus.admin.domain.Links;
import org.venus.admin.domain.LinksDao;

public interface ILinksService {
    List<Links> lists();
    void add(LinksDao ld);
}
