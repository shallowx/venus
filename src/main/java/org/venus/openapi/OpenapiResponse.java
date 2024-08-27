package org.venus.openapi;

import lombok.*;

import java.time.LocalDateTime;

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

    public static OpenapiResponse form(OpenapiEntity entity) {
        return OpenapiResponse.builder()
                .code(entity.getCode())
                .originalUrl(entity.getOriginalUrl())
                .redirect(entity.getRedirect())
                .expiresAt(entity.getExpiresAt())
                .isActive(entity.getIsActive() != 0)
                .build();
    }
}
