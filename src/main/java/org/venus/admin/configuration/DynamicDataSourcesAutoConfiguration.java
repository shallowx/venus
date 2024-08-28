package org.venus.admin.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(DatasourceProperties.class)
public class DynamicDataSourcesAutoConfiguration {
    public static final String DEFAULT_DATASOURCE_NAME = "default";

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(DatasourceProperties properties) {
        DataSource defaultDataSource = defaultDataSource(properties);
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.addDataSource(DEFAULT_DATASOURCE_NAME, defaultDataSource);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        return dynamicDataSource;
    }

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
