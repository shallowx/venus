package org.venus.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "links")
public class LinksEntity {
    @Id
    private long id;
    @Column(name = "code", unique = true)
    private String code;
    @Column(name = "redirect", unique = true)
    private int redirect;
    @Column(name = "original_url", unique = true, length = 500)
    private String originalUrl;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT")
    private short isActive;
}
