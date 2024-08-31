package org.venus.openapi;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenapiGeoRequest {
    private long id;
    @NotNull(message = "The openapi geo lat cannot be empty")
    private double lat;
    @NotNull(message = "The openapi geo lng cannot be empty")
    private double lng;
    @NotNull(message = "The openapi geo clickId cannot be empty")
    private long clickId;
    @NotNull(message = "The openapi geo city cannot be empty")
    private String city;
    @NotNull(message = "The openapi geo country cannot be empty")
    private String country;
}
