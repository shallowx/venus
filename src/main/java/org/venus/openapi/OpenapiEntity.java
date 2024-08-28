package org.venus.openapi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.venus.admin.domain.LinksDao;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "links")
public class OpenapiEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 516186319936155584L;

    @Id
    private long id;
    @Column(name = "code")
    private String code;
    @Column(name = "redirect")
    private int redirect;
    @Column(name = "original_url")
    private String originalUrl;
    @Column(name = "created_at")
    private transient LocalDateTime createdAt;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "is_active")
    private short isActive;

    public static OpenapiEntity from(LinksDao dao) {
        return OpenapiEntity.builder()
                .id(dao.getId())
                .redirect(dao.getRedirect())
                .code(dao.getCode())
                .originalUrl(dao.getOriginalUrl())
                .expiresAt(dao.getExpiresAt())
                .isActive(dao.getIsActive())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenapiEntity entity = (OpenapiEntity) o;
        return  redirect == entity.redirect
                && isActive == entity.isActive
                && Objects.equals(code, entity.code)
                && Objects.equals(originalUrl, entity.originalUrl)
                && Objects.equals(createdAt, entity.createdAt)
                && Objects.equals(expiresAt, entity.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, redirect, originalUrl, createdAt, expiresAt, isActive);
    }
}
