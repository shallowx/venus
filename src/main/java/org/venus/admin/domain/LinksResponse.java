package org.venus.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.venus.admin.annotations.FutureDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LinksResponse {
    @Id
    private long id;
    private String code;
    private short redirect;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    public static LinksResponse from(Links links) {
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

    public static List<LinksResponse> from(List<Links> links) {
        if (links == null || links.isEmpty()) {
            return Collections.emptyList();
        }

        List<LinksResponse> responses = new ArrayList<>(links.size());
        for (Links link : links) {
            responses.add(LinksResponse.from(link));
        }
        return responses;
    }
}
