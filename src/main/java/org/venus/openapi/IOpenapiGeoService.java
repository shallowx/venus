package org.venus.openapi;

/**
 * Interface for the OpenAPI geolocation service.
 *
 * Provides a method to report geolocation data encapsulated in the
 * {@link OpenapiGeoEntity} object.
 */
public interface IOpenapiGeoService {
    /**
     * Reports geolocation data represented by an {@link OpenapiGeoEntity}.
     *
     * @param entity The geolocation entity containing latitude, longitude, city, and country information.
     * @return true if the operation was successful, false otherwise.
     */
    boolean report(OpenapiGeoEntity entity);
}
