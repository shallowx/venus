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

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JpaProperties.class)
public class OpenJpaAutoConfiguration extends JpaBaseConfiguration {

    protected OpenJpaAutoConfiguration(DataSource dataSource,
                                       JpaProperties properties,
                                       ObjectProvider<JtaTransactionManager> jtaTransactionManager) {
        super(dataSource, properties, jtaTransactionManager);
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

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
