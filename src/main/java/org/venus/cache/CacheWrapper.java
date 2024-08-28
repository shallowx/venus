package org.venus.cache;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class CacheWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = -3390119726919797623L;

    private String key;
    private Object value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheWrapper wrapper = (CacheWrapper) o;
        return Objects.equals(key, wrapper.key) && Objects.equals(value, wrapper.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key) + value.hashCode();
    }
}
