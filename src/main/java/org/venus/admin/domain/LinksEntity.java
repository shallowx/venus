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

/**
 * Entity class representing a link in the system.
 * This class maps to the "links" table in the database.
 * It holds various attributes related to a link such as its code, original URL, creation, and expiration dates, etc.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "links")
public class LinksEntity {
    /**
     * Unique identifier for each link.
     */
    @Id
    private long id;
    /**
     * The unique code associated with the link.
     * This value is used for identifying and accessing the specific link.
     */
    @Column(name = "code", unique = true)
    private String code;
    /**
     * The redirect status of the link.
     * This field is mapped to the "redirect" column in the "links" table and is unique.
     */
    @Column(name = "redirect", unique = true)
    private int redirect;
    /**
     * The original URL associated with this link entity.
     * This field is mapped to the "original_url" column in the "links" table.
     * It must be unique and can have a maximum length of 500 characters.
     */
    @Column(name = "original_url", unique = true, length = 500)
    private String originalUrl;
    /**
     * The timestamp indicating when the link was created.
     * Mapped to the "created_at" column in the "links" table.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    /**
     * The date and time when the link expires.
     * Mapped to the "expires_at" column in the "links" table of the database.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    /**
     * Indicates whether the link is active.
     * This field is mapped to the "is_active" column in the "links" table.
     * A value of 1 typically represents active, while 0 represents inactive.
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT")
    private short isActive;
}
