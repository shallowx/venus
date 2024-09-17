package org.venus.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class VenusInitializerAutoConfiguration {
    @Bean(initMethod = "init")
    public VenusInitializer venusInitializer(ApplicationContext context) {
        return new VenusInitializer(context);
    }
}
