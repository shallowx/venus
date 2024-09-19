package org.venus.admin.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Auto-configuration for setting up dynamic data sources in a Spring application.
 *
 * This configuration class sets up a primary dynamic data source bean and a default data source.
 * It uses properties from {@link DatasourceProperties} to configure the default data source.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DatasourceProperties.class)
public class DynamicDataSourcesAutoConfiguration {
    /**
     * The name of the default datasource used in the dynamic datasource configuration.
     *
     * This constant represents the default datasource name and is used to identify the
     * primary datasource within the {@link DynamicDataSourcesAutoConfiguration} class.
     * It is essential for setting up the default target datasource and adding it to the
     * dynamic datasource map.
     *
     * The default datasource configuration is typically populated using properties from
     * {@link DatasourceProperties}.
     *
     * This name is referenced when adding the default datasource to the {@link DynamicDataSource},
     * ensuring that it can be dynamically managed and switched as needed.
     *
     * Usage in context:
     * {@link DynamicDataSourcesAutoConfiguration#dynamicDataSource(DatasourceProperties)}
     */
    public static final String DEFAULT_DATASOURCE_NAME = "default";

    /**
     * Defines a primary dynamic data source bean configured with default properties.
     *
     * @param properties The properties required to configure the default datasource.
     * @return A configured instance of {@link DynamicDataSource}.
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(DatasourceProperties properties) {
        DataSource defaultDataSource = defaultDataSource(properties);
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.addDataSource(DEFAULT_DATASOURCE_NAME, defaultDataSource);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        return dynamicDataSource;
    }

    /**
     * Creates and configures the default DataSource using properties from
     * {@link DatasourceProperties}.
     *
     * @param properties the properties for configuring the default DataSource.
     * @return the configured default DataSource instance.
     */
    private DataSource defaultDataSource(DatasourceProperties properties) {
        String driverClassName = properties.getDriverClassName();
        String url = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .type(HikariDataSource.class)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    public static class DynamicDataSource extends AbstractRoutingDataSource {
        private final Map<Object, Object> dataSourceMap = new HashMap<>(10);

        @Override
        protected Object determineCurrentLookupKey() {
            return DynamicDataSourceContextHolder.getDataSourceName();
        }

        public void addDataSource(final String dataSourceName, final DataSource dataSource) {
            dataSourceMap.put(dataSourceName, dataSource);
            setTargetDataSources(dataSourceMap);
            afterPropertiesSet();
        }
    }

    public static class DynamicDataSourceContextHolder {
        private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

        public static String getDataSourceName() {
            return CONTEXT_HOLDER.get();
        }

        public static void setDataSourceName(final String dataSourceName) {
            CONTEXT_HOLDER.set(dataSourceName);
        }
    }
}
