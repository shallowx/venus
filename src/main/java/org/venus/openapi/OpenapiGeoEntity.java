package org.venus.openapi;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a geolocation entity in the OpenAPI service.
 *
 * This class is a JPA entity mapped to the "geo" table, containing fields for
 * latitude, longitude, city, and country, along with an identifier for the
 * geolocation data and a click event identifier.
 *
 * Annotations used:
 * - `@Getter`, `@Setter` from Lombok for generating getter and setter methods.
 * - `@AllArgsConstructor` and `@NoArgsConstructor` from Lombok for generating constructors.
 * - `@Builder` from Lombok for the builder pattern implementation.
 * - `@Entity` for marking this class as a JPA entity.
 * - `@Table(name = "geo")` for specifying the database table name.
 * - `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)` for primary key generation.
 * - `@Column(name = "...")` for mapping fields to specific columns in the table.
 *
 * Methods:
 * - `from(OpenapiGeoRequest request)`: Static method for creating an instance of this entity
 *   from an OpenapiGeoRequest object.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "geo")
public class OpenapiGeoEntity {
    /**
     * Unique identifier for the geolocation data.
     *
     * This field is the primary key for the "geo" table and its value
     * is automatically generated using the `GenerationType.IDENTITY` strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Represents the latitude of the geolocation.
     *
     * This field is mapped to the "latitude" column in the database. It stores the
     * latitude coordinate value, which is a double.
     */
    @Column(name = "latitude")
    private double lat;
    /**
     * Longitude value of the geolocation associated with the OpenAPI service.
     *
     * This field is mapped to the "longitude" column in the "geo" table.
     */
    @Column(name = "longitude")
    private double lng;
    /**
     * Identifier for the click event associated with the geolocation data.
     *
     * This field is mapped to the "click_id" column in the database table "geo".
     * It is used to uniquely associate a geolocation record with a specific click event.
     */
    @Column(name = "click_id")
    private long clickId;
    /**
     * The city name associated with the geolocation data.
     *
     * This field is mapped to the "city" column in the "geo" table. It represents
     * the urban area corresponding to the provided latitude and longitude values.
     *
     * Validation:
     * - Must not be null or empty.
     */
    @Column(name = "city")
    private String city;
    /**
     * Represents the country name for the geolocation.
     *
     * This field is mapped to the "country" column in the "geo" table.
     * It holds the country name as a string and is an essential part
     * of the geolocation data.
     */
    @Column(name = "country")
    private String country;

    /**
     * Converts an OpenapiGeoRequest object to an OpenapiGeoEntity object.
     *
     * @param request The request payload containing geolocation information.
     * @return An OpenapiGeoEntity object built from the provided request data.
     */
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
