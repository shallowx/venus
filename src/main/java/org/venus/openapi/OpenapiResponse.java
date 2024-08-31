package org.venus.openapi;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OpenapiResponse {
    private String code;
    private int redirect;
    private String originalUrl;
    private LocalDateTime expiresAt;
    private boolean isActive;

    public static OpenapiResponse from(OpenapiEntity entity) {
        return OpenapiResponse.builder()
                .code(entity.getCode())
                .originalUrl(entity.getOriginalUrl())
                .redirect(entity.getRedirect())
                .expiresAt(entity.getExpiresAt())
                .isActive(entity.getIsActive() != 0)
                .build();
    }

    public static List<OpenapiResponse> from(List<OpenapiEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(OpenapiResponse::from)
                .collect(Collectors.toList());
    }
}
