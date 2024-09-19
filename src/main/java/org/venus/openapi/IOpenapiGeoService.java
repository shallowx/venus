package org.venus.openapi;

/**
 * Interface for the OpenAPI geolocation service.
 *
 * Provides a method to report geolocation data encapsulated in the
 * {@link OpenapiGeoEntity} object.
 */
public interface IOpenapiGeoService {
    /**
     * Reports the geolocation data encapsulated in the given {@link OpenapiGeoEntity}.
     *
     * This method is responsible for handling and processing geolocation data
     * provided by the client. The data is encapsulated within an instance of
     * {@link OpenapiGeoEntity}.
     *
     * @param entity The {@link OpenapiGeoEntity} object that contains the geolocation data
     *               to be reported. Must not be null and should contain valid geolocation details.
     */
    void report(OpenapiGeoEntity entity);
}
