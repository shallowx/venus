package org.venus.admin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statistics")
public class StatisticsEntity {
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
}
