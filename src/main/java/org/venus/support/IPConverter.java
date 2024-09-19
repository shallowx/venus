package org.venus.support;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.SneakyThrows;

/**
 * The IPConverter class extends the ClassicConverter to provide the functionality
 * to convert logging events into their corresponding IP address.
 *
 * This class overrides the convert method to obtain the IP address of the machine
 * from which the logging event is generated. It utilizes the InetAddressEndpoint class
 * to fetch the IP address efficiently.
 */
public class IPConverter extends ClassicConverter {

    /**
     * Converts a logging event into a string representation of the machine's IP address.
     *
     * @param event the logging event that triggered the conversion
     * @return the IP address of the machine as a string
     */
    @SneakyThrows
    @Override
    public String convert(ILoggingEvent event) {
        return InetAddressEndpoint.getInetAddress().getHostAddress();
    }
}
