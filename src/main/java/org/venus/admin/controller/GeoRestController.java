package org.venus.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.domain.Geo;
import org.venus.admin.support.GenericListRestApiResponse;

@RestController
@RequestMapping("/geo")
public class GeoRestController {

    @GetMapping("/lists")
    public GenericListRestApiResponse<Geo> lists() {
        return null;
    }
}
