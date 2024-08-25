package org.venus.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "links")
public class Links {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "code",unique = true)
    private String code;
    @Column(name = "original_url",unique = true, length = 500)
    private String originalUrl;
    @Column(name = "created_at")
    private LocalTime createdAt;
    @Column(name = "expires_at")
    private LocalTime expiresAt;
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT")
    private short isActive;
}
