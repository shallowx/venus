package org.venus.admin.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.entiy.Geo;
import org.venus.admin.support.RestApiResponse;

@RestController(value = "geo/")
public class GeoRestController {

    @GetMapping("lists")
    public RestApiResponse<List<Geo>> lists() {
        return null;
    }
}
