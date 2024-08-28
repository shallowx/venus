package org.venus.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jakarta.servlet.ServletContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ListenerSourceSupport {

    private static Environment env;

    public static void initialize(Environment environment) {
        if (environment == null) {
            throw new IllegalStateException("Environment not initialized. Call initialize() first.");
        }
        env = environment;
    }

    public static void initializeFromContext(ServletContext servletContext) {
        if (servletContext != null) {
            Environment env = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getEnvironment();
            initialize(env);
        }
    }

    public static String getSourceAddress() throws UnknownHostException {
        String host = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port", "9527");
        return host + ":" + port;
    }
}
