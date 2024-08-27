package org.venus.admin.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LinksResponse {
    private long id;
    private String code;
    private int redirect;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    public static LinksResponse from(LinksEntity links) {
        return LinksResponse.builder()
                .id(links.getId())
                .code(links.getCode())
                .redirect(links.getRedirect())
                .originalUrl(links.getOriginalUrl())
                .createdAt(links.getCreatedAt())
                .expiresAt(links.getExpiresAt())
                .isActive(links.getIsActive() != 0)
                .build();
    }

    public static List<LinksResponse> from(List<LinksEntity> links) {
        if (links == null || links.isEmpty()) {
            return Collections.emptyList();
        }
        return links.stream()
                .map(LinksResponse::from)
                .collect(Collectors.toList());
    }
}
