package org.venus.admin.domain;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GeoResponse {
    private long id;
    private double lat;
    private double lng;
    private long clickId;
    private String city;
    private String country;

    public static GeoResponse from(GeoEntity geo) {
        return GeoResponse.builder()
                .id(geo.getId())
                .country(geo.getCountry())
                .lat(geo.getLat())
                .city(geo.getCity())
                .clickId(geo.getClickId())
                .build();
    }

    public static List<GeoResponse> from(List<GeoEntity> geos) {
        if (geos == null || geos.isEmpty()) {
            return Collections.emptyList();
        }
        return geos.stream()
                .map(GeoResponse::from)
                .collect(Collectors.toList());
    }
}
