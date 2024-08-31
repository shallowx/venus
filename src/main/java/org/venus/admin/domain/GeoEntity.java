package org.venus.admin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "geo")
public class GeoEntity {
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
}
