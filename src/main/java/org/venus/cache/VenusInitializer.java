package org.venus.cache;

import jakarta.servlet.ServletContext;
import org.springframework.context.ApplicationContext;

public class VenusInitializer {
    private final ApplicationContext applicationContext;

    public VenusInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init() {
        ServletContext servletContext = ((org.springframework.web.context.WebApplicationContext) applicationContext).getServletContext();
        ListenerSourceSupport.initializeFromContext(servletContext);
    }
}
