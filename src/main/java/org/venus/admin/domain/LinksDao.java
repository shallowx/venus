package org.venus.admin.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.venus.admin.service.DefaultBase62Encoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class LinksDao {
    private long id;
    private String code;
    private int redirect;
    private String originalUrl;
    private LocalDateTime expiresAt;
    private short isActive;

    public static LinksDao fromEntity(LinksRequest request) {
        String mappingCode = request.getCode();
        if (mappingCode == null || mappingCode.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Links URl mapping code: {}", mappingCode);
            }
            mappingCode = DefaultBase62Encoder.INSTANCE.encode(request.getId());
        }

        return LinksDao.builder()
                .id(request.getId())
                .code(mappingCode)
                .redirect(request.getRedirect())
                .originalUrl(request.getOriginalUrl())
                .expiresAt(LocalDateTime.parse(request.getExpiresAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .isActive(request.getIsActive())
                .build();
    }
}
