package org.venus.cache;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ValueWrapper {
    private long id;
    private String code;
    private int redirect;
    private String originalUrl;
    private LocalDateTime expiresAt;
    private short isActive;
}
