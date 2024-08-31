package org.venus.openapi;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "statistics")
public class OpenapiStatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "link_id")
    private long linkId;
    @Column(name = "ip")
    private String ip;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "referer")
    private String referer;
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

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
