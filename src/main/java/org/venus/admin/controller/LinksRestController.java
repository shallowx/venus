package org.venus.admin.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.entiy.Links;
import org.venus.admin.support.RestApiResponse;

@RestController(value = "links/")
public class LinksRestController {

    @GetMapping("lists")
    public RestApiResponse<List<Links>> lists() {
        return null;
    }
}
