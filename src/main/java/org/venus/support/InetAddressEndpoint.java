package org.venus.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;

/**
 * The InetAddressEndpoint class provides utility methods to retrieve the machine's InetAddress and hostname.
 * It manages a cache of the InetAddress to avoid redundant calls and reduce latency.
 */
public class InetAddressEndpoint {

    /**
     * Logger instance used for logging events and messages within the InetAddressEndpoint class.
     * This logger is configured to output logs for the InetAddressEndpoint class context.
     */
    private static final Logger logger = LoggerFactory.getLogger(InetAddressEndpoint.class);
    /**
     * Cached instance of InetAddress used to avoid repeated network interface lookups.
     *
     * The static variable is initialized the first time the innerInetAddress method is invoked,
     * and it is reused for subsequent calls to improve performance.
     */
    private static InetAddress cachedInetAddress = null;
    /**
     * The hostname associated with the network endpoint.
     * This variable is initially set to null and is typically
     * used to cache the hostname for an InetAddress endpoint
     * after it has been resolved.
     */
    private static String hostName = null;
    /**
     * A static variable representing the expiration time for the cached InetAddress.
     * This is used to determine when the cache should be refreshed.
     */
    private static long expires = 0;
    /**
     * Maximum time in milliseconds that an InetAddress instance is considered valid before it expires.
     * This is set to 5 minutes (5 * 60 * 1000 milliseconds).
     */
    private static final long MAX_EXPIRES = 5 * 60 * 1000L;
    /**
     * A static final lock object used for synchronizing access to shared resources
     * within the InetAddressEndpoint class. Ensures thread-safe operations when
     * accessing or modifying the shared state.
     */
    private static final Object lock = new Object();
    /**
     * The file path to the host name file used to determine the host name
     * in Unix-based systems.
     */
    private static final String ETC = "/etc/hostname";

    /**
     * Retrieves the cached IP address or computes a new one if the cache has expired.
     *
     * @return the IP address of the current machine.
     * @throws SocketException if an error occurs while attempting to access the network interfaces of the machine.
     * @throws UnknownHostException if the local host name could not be resolved into an address.
     */
    public static InetAddress getInetAddress() throws SocketException, UnknownHostException {
        InetAddress inetAddress = null;
        synchronized (lock) {
            long now = System.currentTimeMillis();
            if (cachedInetAddress != null) {
                if ((now - expires) < MAX_EXPIRES) {
                    inetAddress = cachedInetAddress;
                } else {
                    cachedInetAddress = null;
                }
            }

            if (inetAddress == null) {
                InetAddress localHost = innerInetAddress();
                cachedInetAddress = localHost;
                expires = now;
                inetAddress = localHost;
            }
        }
        return inetAddress;
    }

    /**
     * Retrieves the host name of the machine. If the hostname has not been cached
     * yet, it attempts to read it from a file specified by the ETC constant. If the
     * file does not exist or an error occurs during reading, it falls back to the
     * default hostname of the local machine. If the resulting hostname is "localhost",
     * it sets the cached hostname to null. If all retrieval attempts fail, it defaults
     * to "localhost".
     *
     * @return the host name of the machine, or "localhost" if it cannot be determined
     */
    public static String hostName() {
        if (hostName == null) {
            synchronized (lock) {
                File f = new File(ETC);
                if (f.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        hostName = br.readLine();
                    } catch (IOException e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("Read hostname is failure", e);
                        }
                    }
                }

                if (hostName == null) {
                    try {
                        hostName = InetAddress.getLocalHost().getHostName();
                        if (Objects.equals(hostName, "localhost")) {
                            hostName = null;
                        }
                    } catch (UnknownHostException e) {
                        hostName = "localhost";
                    }
                }
            }
        }
        return hostName;
    }

    /**
     * Retrieves the first non-loopback and non-IPv6 site-local address available
     * on any network interface. If no site-local address is found, returns the
     * first non-loopback and non-IPv6 address. If no such address is found,
     * returns the local host address.
     *
     * @return An {@link InetAddress} object representing the found address or
     * the local host address if no suitable address is found.
     * @throws SocketException If an I/O error occurs.
     * @throws UnknownHostException If the local host name could not be resolved
     * into an address.
     */
    private static InetAddress innerInetAddress() throws SocketException, UnknownHostException {
        InetAddress address = null;
        for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
            NetworkInterface ni = e.nextElement();
            for (Enumeration<InetAddress> ee = ni.getInetAddresses(); ee.hasMoreElements(); ) {
                InetAddress addr = ee.nextElement();
                if (!addr.isLoopbackAddress() && !(addr instanceof Inet6Address)) {
                    if (addr.isSiteLocalAddress()) {
                        return addr;
                    } else if (address == null) {
                        address = addr;
                    }
                }
            }
        }
        if (address != null) {
            return address;
        }
        return InetAddress.getLocalHost();
    }
}
