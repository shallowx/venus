package org.venus.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.SneakyThrows;

public class IPConverter extends ClassicConverter {

    @SneakyThrows
    @Override
    public String convert(ILoggingEvent event) {
        return InetAddressEndpoint.getInetAddress().getHostAddress();
    }
}
