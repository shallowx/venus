package org.venus.openapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenapiStatisticsRequest {
    private long id;
    private long linkId;
    @NotNull(message = "The openapi statistics ip cannot be empty")
    private String ip;
    @NotNull(message = "The openapi statistics userAgent cannot be empty")
    private String userAgent;
    @NotNull(message = "The openapi statistics referer cannot be empty")
    private String referer;
    @NotNull(message = "The openapi statistics clickedAt cannot be empty")
    private LocalDateTime clickedAt;
}
