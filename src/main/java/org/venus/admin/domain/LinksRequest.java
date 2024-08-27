package org.venus.admin.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.venus.admin.annotations.FutureDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LinksRequest {

    /**
     * need to ensure {@code id} its unique
     */
    @NotNull(message = "The links mapping id cannot be empty")
    @Min(value = 0, message = "The links mapping id min-value is 0")
    @Max(value = Long.MAX_VALUE, message = "The links mapping id max-value is 0x7fffffffffffffffL")
    private long id;

    /**
     *  if {@code code} is not null, need to ensure its unique and its length must be 8, and it needs to conform to 62-bit encoded characters, eg:[0-9] [a-z] [A-Z]
     *  if its null, it will create by venus system.
     */
    @Length(min = 0, max = 8, message = "The links code character length must be 8")
    private String code;

    @NotNull(message = "The links redirect code cannot be empty")
    @Min(value = 301, message = "The min redirect must be 301")
    @Max(value = 302, message = "The max redirect must be 302")
    private int redirect;

    @NotEmpty(message = "The links original URL cannot be empty")
    @Length(min = 0, max = 500, message = "The links original URL length in [0,500]")
    private String originalUrl;

    @NotNull(message = "The links expires_at cannot be empty")
    @FutureDate
    private String expiresAt;

    @NotNull
    @Min(value = 0, message = "The links is_active min-value is 0")
    @Max(value = 1, message = "The links is_active max-value is 1")
    private short isActive;
}
