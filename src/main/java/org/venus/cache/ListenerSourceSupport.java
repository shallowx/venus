package org.venus.cache;

import jakarta.servlet.ServletContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A support class to handle listener source initialization and address retrieval in a web application.
 *
 * This class provides methods to initialize the environment from different contexts and to obtain
 * the source address of the current instance.
 */
public class ListenerSourceSupport {
    /**
     * The environment configuration used by the ListenerSourceSupport class.
     *
     * This static variable holds the application environment settings,
     * which are necessary for initializing and retrieving the source
     * address within a web application context.
     *
     * It must be initialized before accessing the environment-specific properties,
     * typically done through the {@link ListenerSourceSupport#initialize(Environment)}
     * or {@link ListenerSourceSupport#initializeFromContext(ServletContext)} methods.
     */
    private static Environment env;

    /**
     * Initializes the environment for the ListenerSourceSupport class.
     *
     * This method sets the static environment variable to the provided Environment instance,
     * allowing other methods within the class to interact with the environment.
     *
     * @param environment the Environment instance to initialize; must not be null
     * @throws IllegalStateException if the provided environment is null
     */
    public static void initialize(Environment environment) {
        if (environment == null) {
            throw new IllegalStateException("Environment not initialized. Call initialize() first.");
        }
        env = environment;
    }

    /**
     * Initializes the environment using the given ServletContext.
     * This method retrieves the required WebApplicationContext from the servlet context,
     * obtains its environment, and initializes the static environment variable of the class.
     *
     * @param servletContext the ServletContext from which to retrieve the WebApplicationContext and its environment
     */
    public static void initializeFromContext(ServletContext servletContext) {
        if (servletContext != null) {
            Environment env = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getEnvironment();
            initialize(env);
        }
    }

    /**
     * Retrieves the source address of the current instance.
     *
     * The method constructs the source address by combining the local host address
     * and the server port from the environment configuration.
     *
     * @return A string representing the source address in the format "host:port".
     * @throws UnknownHostException If the local host name could not be resolved into an address.
     */
    public static String getSourceAddress() throws UnknownHostException {
        String host = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port", "8029");
        return host + ":" + port;
    }
}
