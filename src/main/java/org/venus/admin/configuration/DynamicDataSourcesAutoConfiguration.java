package org.venus.admin.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class DynamicDataSourcesAutoConfiguration {

    public static final String DRIVER_CLASS_NAME = "spring.datasource.default.driver-class-name";
    public static final String DATASOURCE_URL = "spring.datasource.default.url";
    public static final String DATASOURCE_USERNAME = "spring.datasource.default.username";
    public static final String DATASOURCE_PASSWORD = "spring.datasource.default.password";
    public static final String DEFAULT_DATASOURCE_NAME = "default";

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(final Environment environment) {
        DataSource defaultDataSource = defaultDataSource(environment);
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.addDataSource(DEFAULT_DATASOURCE_NAME, defaultDataSource);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        return dynamicDataSource;
    }

    private DataSource defaultDataSource(final Environment environment) {
        String driverName = environment.getProperty(DRIVER_CLASS_NAME);
        String url = environment.getProperty(DATASOURCE_URL);
        String username = environment.getProperty(DATASOURCE_USERNAME);
        String password = environment.getProperty(DATASOURCE_PASSWORD);
        return DataSourceBuilder.create()
                .driverClassName(driverName)
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
