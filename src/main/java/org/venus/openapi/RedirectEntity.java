package org.venus.openapi;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@ToString
public class RedirectEntity {
    private int redirect;
    private String originalUrl;
}
