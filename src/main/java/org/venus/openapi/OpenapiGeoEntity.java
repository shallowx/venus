package org.venus.openapi;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "geo")
public class OpenapiGeoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "latitude")
    private double lat;
    @Column(name = "longitude")
    private double lng;
    @Column(name = "click_id")
    private long clickId;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;

    public static OpenapiGeoEntity from(OpenapiGeoRequest request) {
        return OpenapiGeoEntity.builder()
                .lat(request.getLat())
                .lng(request.getLng())
                .clickId(request.getClickId())
                .city(request.getCity())
                .country(request.getCountry())
                .build();
    }
}
