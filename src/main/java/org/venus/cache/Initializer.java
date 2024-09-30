package org.venus.cache;

import jakarta.servlet.ServletContext;
import org.springframework.context.ApplicationContext;

/**
 * VenusInitializer is responsible for initializing the web application context
 * for Venus-specific configurations.
 *
 * It ensures that the ListenerSourceSupport class is properly initialized with the
 * servlet context obtained from the ApplicationContext.
 */
public class Initializer {
    /**
     * The application context used for initializing the web application context
     * specific to Venus configurations.
     *
     * This context provides the necessary configurations and resources required
     * to initialize and manage the application's lifecycle within a web environment.
     */
    private final ApplicationContext applicationContext;

    /**
     * Constructs a new instance of VenusInitializer with the given ApplicationContext.
     *
     * @param applicationContext the ApplicationContext to be used for initialization; must not be null
     */
    public Initializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Initializes the application by setting up the ListenerSourceSupport class
     * with the servlet context obtained from the application context.
     *
     * This method retrieves the ServletContext from the WebApplicationContext
     * and passes it to ListenerSourceSupport to initialize its environment.
     */
    public void init() {
        ServletContext servletContext = ((org.springframework.web.context.WebApplicationContext) applicationContext).getServletContext();
        ListenerSourceSupport.initializeFromContext(servletContext);
    }
}
