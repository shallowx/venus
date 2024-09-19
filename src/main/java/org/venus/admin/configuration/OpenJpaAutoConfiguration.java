package org.venus.admin.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Auto-configuration class for setting up OpenJPA with Spring Boot.
 * This class extends JpaBaseConfiguration to provide necessary configuration
 * for OpenJPA, leveraging the properties defined in JpaProperties.
 *
 * The OpenJpaAutoConfiguration class sets various OpenJPA specific properties,
 * like establishing proper logging mechanisms and enhancing class loading.
 *
 * It requires a DataSource, JpaProperties, and an ObjectProvider for JtaTransactionManager
 * to be injected during the construction of the class.
 *
 * The createJpaVendorAdapter method ensures that a HibernateJpaVendorAdapter is used.
 * The getVendorProperties method sets specific OpenJPA vendor properties.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JpaProperties.class)
public class OpenJpaAutoConfiguration extends JpaBaseConfiguration {

    /**
     * Constructor for OpenJpaAutoConfiguration.
     *
     * @param dataSource The data source to be used by the JPA provider.
     * @param properties The JPA properties defined in the application configuration.
     * @param jtaTransactionManager The provider for JtaTransactionManager, if any.
     */
    protected OpenJpaAutoConfiguration(DataSource dataSource,
                                       JpaProperties properties,
                                       ObjectProvider<JtaTransactionManager> jtaTransactionManager) {
        super(dataSource, properties, jtaTransactionManager);
    }

    /**
     * Creates a JPA vendor adapter specific to the persistence provider.
     *
     * @return an instance of HibernateJpaVendorAdapter as the JPA vendor adapter
     */
    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    /**
     * Provides OpenJPA-specific vendor properties necessary for configuration.
     * These properties control various aspects such as schema synchronization, logging,
     * and enhancements during runtime.
     *
     * @return a Map containing key-value pairs of OpenJPA configuration properties
     */
    @Override
    protected Map<String, Object> getVendorProperties() {
        final Map<String, Object> result = new HashMap<>();
        result.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
        result.put("openjpa.ClassLoadEnhancement", "false");
        result.put("openjpa.DynamicEnhancementAgent", "false");
        result.put("openjpa.RuntimeUnenhancedClasses", "supported");
        result.put("openjpa.Log", "slf4j");
        return result;
    }
}
