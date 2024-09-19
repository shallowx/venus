package org.venus.openapi;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * OpenapiStatisticsEntity is an entity representing the OpenAPI statistics information.
 * This class maps to the "statistics" table in the database.
 *
 * It contains information relevant to OpenAPI link statistics such as link id, ip address,
 * user agent, referer, and the timestamp when the link was clicked.
 *
 * The class uses Lombok annotations to reduce boilerplate code by generating getters,
 * setters, constructors, and builder pattern methods automatically.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "statistics")
public class OpenapiStatisticsEntity {
    /**
     * The unique identifier for the OpenAPI statistics entry.
     *
     * This identifier is automatically generated using the identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Represents the unique identifier for the link associated with the OpenAPI statistics.
     * This field is mapped to the "link_id" column in the database.
     */
    @Column(name = "link_id")
    private long linkId;
    /**
     * The IP address from which an OpenAPI request was made.
     * This field is mapped to the "ip" column in the database.
     */
    @Column(name = "ip")
    private String ip;
    /**
     * Represents the user agent string associated with an OpenAPI statistics record.
     * This field captures the information about the client's software environment
     * such as the browser, operating system, and other relevant details.
     */
    @Column(name = "user_agent")
    private String userAgent;
    /**
     * Represents the HTTP referer in the OpenAPI statistics entity.
     * This field captures the URL of the page that referred to the current request
     * and is mapped to the "referer" column in the database.
     */
    @Column(name = "referer")
    private String referer;
    /**
     * Represents the timestamp when an OpenAPI link was clicked.
     *
     * This field is stored in the "clicked_at" column of the "statistics" table.
     * It is used to track the exact date and time of user interaction.
     */
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    /**
     * Converts an OpenapiStatisticsRequest to an OpenapiStatisticsEntity.
     *
     * @param request the OpenapiStatisticsRequest containing the details to be converted into an entity
     * @return an OpenapiStatisticsEntity built from the details of the provided request
     */
    public static OpenapiStatisticsEntity from(OpenapiStatisticsRequest request) {
        return OpenapiStatisticsEntity.builder()
                .id(request.getId())
                .clickedAt(request.getClickedAt())
                .linkId(request.getLinkId())
                .ip(request.getIp())
                .referer(request.getReferer())
                .userAgent(request.getUserAgent())
                .build();
    }
}
