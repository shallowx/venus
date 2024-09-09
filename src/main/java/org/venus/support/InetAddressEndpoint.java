package org.venus.support;

import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;

public class InetAddressEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(InetAddressEndpoint.class);
    private static InetAddress cachedInetAddress = null;
    private static String hostName = null;
    private static long expires = 0;
    private static final long MAX_EXPIRES = 5 * 60 * 1000L;
    private static final Object lock = new Object();
    private static final String ETC = "/etc/hostname";

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
