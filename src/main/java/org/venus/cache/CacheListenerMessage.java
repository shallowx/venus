package org.venus.cache;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@EqualsAndHashCode
public class CacheListenerMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = -2385126650426135507L;

    private String name;
    private CacheMessageListenerType type;
    private String key;
    private Object value;
    private String source;

    @Override
    public String toString() {
        return "CacheListenerMessage{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", source='" + source + '\'' +
                '}';
    }
}
