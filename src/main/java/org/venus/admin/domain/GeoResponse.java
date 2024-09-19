package org.venus.admin.domain;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a response object for geographic data.
 *
 * This class stores information about a geographic entity including its
 * unique identifier, latitude, longitude, associated click identifier, city,
 * and country. It provides methods for converting geographic entities to
 * response objects.
 *
 * The class is used primarily in REST API responses to encapsulate
 * geographic data in a structured format.
 *
 * An instance can be created using a builder or the provided constructors.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GeoResponse {
    /**
     * The unique identifier for the geographic response object.
     *
     * This identifier corresponds to the id field of the GeoEntity and is
     * used to uniquely distinguish each geographic entity in the responses.
     */
    private long id;
    /**
     * The latitude coordinate for the geographic entity.
     *
     * This value is used to represent the north-south position of a geographic
     * entity on the Earth's surface.
     */
    private double lat;
    /**
     * Represents the longitude coordinate for the geographic entity.
     *
     * This field is mapped to the "longitude" column in the "geo" table
     * of the database and is used to store the longitude position data.
     */
    private double lng;
    /**
     * An associated click identifier.
     *
     * This variable stores the identifier for a click event related to the geographic entity.
     * It is used to link the geo response object with a specific click event.
     */
    private long clickId;
    /**
     * The name of the city associated with the geographic entity.
     *
     * This field is mapped to the "city" column in the "geo" table of the database.
     */
    private String city;
    /**
     * Represents the country name associated with this geographic entity.
     *
     * This field is mapped to the "country" column in the "geo" table of the database.
     */
    private String country;

    /**
     * Converts a GeoEntity to a GeoResponse.
     *
     * @param geo the GeoEntity to convert
     * @return a GeoResponse object containing the data from the given GeoEntity
     */
    public static GeoResponse from(GeoEntity geo) {
        return GeoResponse.builder()
                .id(geo.getId())
                .country(geo.getCountry())
                .lat(geo.getLat())
                .city(geo.getCity())
                .clickId(geo.getClickId())
                .build();
    }

    /**
     * Converts a list of GeoEntity objects to a list of GeoResponse objects.
     *
     * @param geos the list of GeoEntity objects to be converted
     * @return a list of GeoResponse objects, or an empty list if the input list is null or empty
     */
    public static List<GeoResponse> from(List<GeoEntity> geos) {
        if (geos == null || geos.isEmpty()) {
            return Collections.emptyList();
        }
        return geos.stream()
                .map(GeoResponse::from)
                .collect(Collectors.toList());
    }
}
