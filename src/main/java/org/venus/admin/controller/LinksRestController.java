package org.venus.admin.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.entity.Links;
import org.venus.admin.support.AbstractRestApiResponse;
import org.venus.admin.support.GenericListRestApiResponse;

@RestController
@RequestMapping(value = "links/")
public class LinksRestController {

    @GetMapping("lists")
    public GenericListRestApiResponse<Links> lists() {
        return null;
    }
}
