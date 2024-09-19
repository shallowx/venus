package org.venus.support;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * The HostNameConverter class extends the ClassicConverter and provides functionality
 * to return the host name of the machine where the application is running.
 * This is typically used in logging frameworks to include the host name in log messages.
 */
public class HostNameConverter extends ClassicConverter {
    /**
     * Converts the given ILoggingEvent to a String representing the hostname
     * of the machine where the application is running.
     *
     * @param event the ILoggingEvent instance containing the log event to be converted
     * @return the hostname of the machine as a String
     */
    @Override
    public String convert(ILoggingEvent event) {
        return InetAddressEndpoint.hostName();
    }
}
