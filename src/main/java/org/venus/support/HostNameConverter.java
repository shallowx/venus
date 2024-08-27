package org.venus.support;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class HostNameConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return InetAddressEndpoint.hostName();
    }
}
