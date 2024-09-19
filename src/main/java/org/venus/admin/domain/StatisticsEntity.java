package org.venus.admin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity class representing statistics information.
 * This class maps to the 'statistics' table in the database.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statistics")
public class StatisticsEntity {
    /**
     * Unique identifier for the statistics entity.
     * This value is automatically generated and corresponds to the primary key
     * in the 'statistics' table of the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Unique identifier for the link associated with this statistic.
     * Maps to the 'link_id' column in the 'statistics' table.
     */
    @Column(name = "link_id")
    private long linkId;
    /**
     * Represents the IP address associated with a statistic entry.
     * This value is mapped to the 'ip' column in the 'statistics' table.
     */
    @Column(name = "ip")
    private String ip;
    /**
     * Represents the user agent string from the HTTP request,
     * associated with a specific statistics entry.
     */
    @Column(name = "user_agent")
    private String userAgent;
    /**
     * Represents the referer URL from which the link was accessed.
     * Corresponds to the 'referer' column in the 'statistics' table.
     */
    @Column(name = "referer")
    private String referer;
    /**
     * Timestamp representing when a link was clicked.
     * This value is stored in the 'clicked_at' column of the 'statistics' table.
     */
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;
}
