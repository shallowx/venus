package org.venus.admin.entiy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statistics")
public class Statistics {
    @Id
    @GeneratedValue
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
