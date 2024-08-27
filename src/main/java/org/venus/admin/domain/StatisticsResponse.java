package org.venus.admin.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class StatisticsResponse {
    private long id;
    private long linkId;
    private String ip;
    private String userAgent;
    private String referer;
    private LocalDateTime clickedAt;

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

    public static List<StatisticsResponse> from(List<StatisticsEntity> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return Collections.emptyList();
        }
        return statistics.stream()
                .map(StatisticsResponse::from)
                .collect(Collectors.toList());
    }

}
