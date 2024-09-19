package org.venus.admin.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A DTO class representing the response structure for statistics data.
 *
 * This class is used to transfer statistics information from the service layer
 * to the client via REST endpoints.
 *
 * An instance of this class typically includes details such as the unique identifier
 * for the statistic entry, associated link ID, IP address, user agent string, referer URL,
 * and the timestamp when the link was clicked.
 *
 * The class provides static methods to facilitate the conversion from a {@link StatisticsEntity}
 * to a {@link StatisticsResponse}, either for a single entity or a list of entities.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class StatisticsResponse {
    /**
     * Unique identifier for the statistic entry.
     *
     * This value is used to distinguish each statistic entry uniquely
     * and is typically generated automatically.
     */
    private long id;
    /**
     * Represents the unique identifier for the link associated with this statistics entry.
     * This value is used to correlate statistic records with their associated links.
     */
    private long linkId;
    /**
     * Represents the IP address associated with a statistic entry.
     * This value is utilized to track the origin IP of the request.
     */
    private String ip;
    /**
     * Represents the user agent string from the HTTP request associated with a specific statistics entry.
     */
    private String userAgent;
    /**
     * Represents the referer URL from which the link was accessed.
     * This information is typically extracted from the HTTP request header
     * and is used to understand the source of the traffic.
     */
    private String referer;
    /**
     * The timestamp representing when the link was clicked.
     * This field holds the precise date and time information
     * of the click event, making it useful for tracking and
     * analyzing click patterns over time.
     */
    private LocalDateTime clickedAt;

    /**
     * Converts a StatisticsEntity to a StatisticsResponse.
     *
     * @param statistics the StatisticsEntity to be converted
     * @return a StatisticsResponse object containing the data from the provided StatisticsEntity
     */
    public static StatisticsResponse from(StatisticsEntity statistics) {
        return StatisticsResponse.builder()
                .id(statistics.getId())
                .linkId(statistics.getLinkId())
                .ip(statistics.getIp())
                .userAgent(statistics.getUserAgent())
                .referer(statistics.getReferer())
                .clickedAt(statistics.getClickedAt())
                .build();
    }

    /**
     * Converts a list of {@link StatisticsEntity} instances to a list of {@link StatisticsResponse} instances.
     *
     * @param statistics the list of {@link StatisticsEntity} to convert
     * @return a list of {@link StatisticsResponse} instances corresponding to the input list;
     *         returns an empty list if the input list is null or empty
     */
    public static List<StatisticsResponse> from(List<StatisticsEntity> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return Collections.emptyList();
        }
        return statistics.stream()
                .map(StatisticsResponse::from)
                .collect(Collectors.toList());
    }

}
