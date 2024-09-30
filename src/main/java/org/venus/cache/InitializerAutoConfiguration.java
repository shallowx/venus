package org.venus.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for initializing Venus-specific configurations in the web application context.
 *
 * This class provides configuration settings and bean definitions needed
 * to set up and initialize the VenusInitializer.
 */
@Configuration(proxyBeanMethods = false)
public class InitializerAutoConfiguration {
    /**
     * Creates a VenusInitializer bean with the specified ApplicationContext.
     * The VenusInitializer is responsible for initializing the web application context
     * for Venus-specific configurations.
     *
     * @param context the ApplicationContext to be used for initialization; must not be null
     * @return a new instance of VenusInitializer initialized with the given ApplicationContext
     */
    @Bean(initMethod = "init")
    public Initializer venusInitializer(ApplicationContext context) {
        return new Initializer(context);
    }
}
