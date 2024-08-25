package org.venus.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.entity.Geo;
import org.venus.admin.repository.GeoRepository;
import org.venus.admin.support.AbstractRestApiResponse;
import org.venus.admin.support.GenericListRestApiResponse;

@RestController
@RequestMapping("geo/")
public class GeoRestController {

    @GetMapping("lists")
    public GenericListRestApiResponse<Geo> lists() {
        return null;
    }
}
