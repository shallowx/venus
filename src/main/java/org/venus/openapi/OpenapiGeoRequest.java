package org.venus.openapi;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request object for geolocation data in the OpenAPI service.
 *
 * This class encapsulates the required fields necessary to report geolocation
 * information including latitude, longitude, city, and country, along with
 * associated metadata such as ID and click ID. Each field is annotated with
 * validation constraints to ensure the integrity of the data.
 *
 * Annotations used:
 * - `@Getter`, `@Setter`: Lombok annotations for generating getter and setter methods.
 * - `@AllArgsConstructor`: Lombok annotation for generating a constructor with all fields.
 * - `@NoArgsConstructor`: Lombok annotation for generating a no-argument constructor.
 * - `@NotNull`: Ensures certain fields cannot have null values and includes custom messages.
 *
 * Fields:
 * - `id`:           Unique identifier for the request.
 * - `lat`:          Latitude value of the geolocation.
 * - `lng`:          Longitude value of the geolocation.
 * - `clickId`:      Identifier for the click event associated with the geolocation data.
 * - `city`:         City name for the geolocation.
 * - `country`:      Country name for the geolocation.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenapiGeoRequest {
    /**
     * Unique identifier for the request.
     */
    private long id;
    /**
     * Latitude value of the geolocation.
     *
     * This field is annotated with `@NotNull` to ensure it cannot be null.
     * The custom message "The openapi geo lat cannot be empty" is displayed
     * if the validation fails.
     */
    @NotNull(message = "The openapi geo lat cannot be empty")
    private double lat;
    /**
     * Longitude value of the geolocation required for the OpenAPI service.
     *
     * This field is annotated with `@NotNull` to ensure it is not null
     * and includes a custom message "The openapi geo lng cannot be empty".
     */
    @NotNull(message = "The openapi geo lng cannot be empty")
    private double lng;
    /**
     * Identifier for the click event associated with the geolocation data.
     * This field is mandatory and should not be null or empty.
     *
     * Validation:
     * - Must be a non-null value.
     *
     * Constraints:
     * - Annotated with `@NotNull` to ensure the value is present.
     *   If the value is null, an error message "The openapi geo clickId cannot be empty" will be triggered.
     */
    @NotNull(message = "The openapi geo clickId cannot be empty")
    private long clickId;
    /**
     * The city name associated with the geolocation data.
     *
     * This field is mandatory and cannot be empty. It represents the urban area
     * corresponding to the provided latitude and longitude values.
     *
     * Validation:
     * - Must not be null or empty.
     */
    @NotNull(message = "The openapi geo city cannot be empty")
    private String city;
    /**
     * Represents the country name for the geolocation in the OpenAPI service.
     *
     * This field is mandatory and should not be empty. It holds the country name
     * as a string and is validated to ensure that it is not null.
     *
     * Validation:
     * - `@NotNull(message = "The openapi geo country cannot be empty")`: Ensures
     *   that the country field is not null and provides a custom message if this
     *   validation fails.
     */
    @NotNull(message = "The openapi geo country cannot be empty")
    private String country;
}
