package org.venus.admin.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.Links;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.repository.LinksRepository;
import org.venus.admin.service.ILinksService;

@Service
@Slf4j
public class LinksServiceImpl implements ILinksService {

    @Autowired
    private LinksRepository linksRepository;

    @Override
    public List<Links> lists() {
        return linksRepository.list();
    }

    @Override
    public void add(LinksDao ld) {
        linksRepository.add(ld);
    }
}
