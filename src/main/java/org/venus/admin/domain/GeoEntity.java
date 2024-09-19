package org.venus.admin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a geographic entity with properties for latitude, longitude, city, and country.
 *
 * This entity is mapped to the "geo" table in the database using JPA annotations.
 *
 * Fields:
 * - id: The unique identifier for the GeoEntity.
 * - lat: The latitude coordinate.
 * - lng: The longitude coordinate.
 * - clickId: An associated click identifier.
 * - city: The city name.
 * - country: The country name.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "geo")
public class GeoEntity {
    /**
     * The unique identifier for the GeoEntity.
     *
     * This field is automatically generated by the database using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Represents the latitude coordinate for the geographic entity.
     *
     * The latitude is stored in the "latitude" column in the database.
     */
    @Column(name = "latitude")
    private double lat;
    /**
     * The longitude coordinate of the geographic entity.
     *
     * This field is mapped to the "longitude" column in the “geo” table in the database.
     */
    @Column(name = "longitude")
    private double lng;
    /**
     * An associated click identifier.
     *
     * This field is mapped to the "click_id" column in the database.
     */
    @Column(name = "click_id")
    private long clickId;
    /**
     * The name of the city associated with the geographic entity.
     *
     * This field is mapped to the "city" column in the "geo" table of the database.
     */
    @Column(name = "city")
    private String city;
    /**
     * Represents the country name associated with this geographic entity.
     *
     * This field is mapped to the "country" column in the "geo" table of the database.
     */
    @Column(name = "country")
    private String country;
}
