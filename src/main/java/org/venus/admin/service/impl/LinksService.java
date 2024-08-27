package org.venus.admin.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.LinksEntity;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.repository.LinksRepository;
import org.venus.admin.service.ILinksService;

@Service
@Slf4j
public class LinksService implements ILinksService {

    @Autowired
    private LinksRepository linksRepository;

    @Override
    public List<LinksEntity> lists() {
        return linksRepository.list();
    }

    @Override
    public LinksEntity get(long id) {
        return linksRepository.get(id);
    }

    @Override
    public void add(LinksDao ld) {
        linksRepository.add(ld);
    }

    @Override
    public void update(LinksDao ld) {
        linksRepository.update(ld);
    }

    @Override
    public void delete(long id) {
        linksRepository.remove(id);
    }
}
